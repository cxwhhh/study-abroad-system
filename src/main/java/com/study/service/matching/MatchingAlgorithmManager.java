package com.study.service.matching;

import com.study.service.matching.algorithm.SchoolMatchingStrategy;
import com.study.service.matching.dto.MatchingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 匹配算法管理器
 * 根据地区选择对应的匹配算法
 */
@Service
public class MatchingAlgorithmManager {

    private static final Logger log = LoggerFactory.getLogger(MatchingAlgorithmManager.class);

    // 利用 Spring 自动注入所有实现类
    @Autowired
    private List<SchoolMatchingStrategy> allStrategies;

    /**
     * 核心调度方法
     */
    public List<MatchingResult> executeMatching(MatchingRequest request) {
        List<MatchingResult> finalResults = new ArrayList<>();
        if (allStrategies == null || allStrategies.isEmpty()) {
            log.warn("系统未注册任何匹配算法");
            return finalResults;
        }

        List<SchoolMatchingStrategy> targetStrategies = allStrategies.stream()
                .filter(s -> s != null && s.supports(request))
                .sorted(Comparator.comparingInt(s -> isGenericUKStrategy(s) ? 1 : 0))
                .collect(Collectors.toList());

        log.info("根据请求 (Region: {}, TargetSchools: {}) 匹配到 {} 个算法策略",
                request.getRegion(), request.getTargetSchools(), targetStrategies.size());

        // 2. 并行或串行执行所有策略
        for (SchoolMatchingStrategy strategy : targetStrategies) {
            try {
                List<MatchingResult> results = strategy.computeMatchingResults(request);
                if (results != null) {
                    finalResults.addAll(results);
                }
            } catch (Exception e) {
                log.error("算法执行异常 [{}]", strategy.getSchoolName(), e);
                // 单个算法失败不应阻断整体流程
            }
        }

        Map<String, MatchingResult> deduped = new LinkedHashMap<>();
        for (MatchingResult r : finalResults) {
            if (r == null || r.getSchoolId() == null || r.getMajorName() == null) {
                continue;
            }
            String key = r.getSchoolId() + "|" + r.getMajorName();
            MatchingResult existing = deduped.get(key);
            if (existing == null) {
                deduped.put(key, r);
                continue;
            }
            boolean existingGeneric = isGenericResult(existing);
            boolean currentGeneric = isGenericResult(r);
            if (existingGeneric != currentGeneric) {
                if (!currentGeneric) {
                    deduped.put(key, r);
                }
                continue;
            }
            double existingScore = existing.getMatchScore() == null ? -1 : existing.getMatchScore();
            double currentScore = r.getMatchScore() == null ? -1 : r.getMatchScore();
            if (currentScore > existingScore) {
                deduped.put(key, r);
            }
        }

        return deduped.values().stream()
                .filter(r -> r.getMatchScore() != null)
                .sorted(
                        Comparator
                                .comparing(MatchingResult::getAdmissionProbability,
                                        Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(MatchingResult::getMatchScore,
                                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean isGenericUKStrategy(SchoolMatchingStrategy strategy) {
        if (strategy == null) {
            return false;
        }
        String name = strategy.getSchoolName();
        return name != null && name.contains("通用");
    }

    private boolean isGenericResult(MatchingResult r) {
        if (r == null) {
            return false;
        }
        String strategy = r.getAlgorithmStrategy();
        if (strategy == null) {
            return false;
        }
        return strategy.toUpperCase().contains("GENERIC");
    }

    // 提供给 Controller 的辅助方法：获取支持的地区
    public List<String> getSupportedRegions() {
        // 动态从算法中获取所有支持的地区并去重
        if (allStrategies == null)
            return new ArrayList<>();
        return allStrategies.stream()
                .map(SchoolMatchingStrategy::getRegion)
                .distinct()
                .collect(Collectors.toList());
    }
}
