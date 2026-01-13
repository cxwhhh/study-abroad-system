package com.study.service.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.schools.School;
import com.study.service.schools.SchoolMapper;

import lombok.extern.slf4j.Slf4j;

import com.study.service.major.CoreCourse;
import com.study.service.major.CoreCourseRepository;
import com.study.service.major.Major;
import com.study.service.major.MajorMapper; // 确保引入

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 匹配结果服务
 * 提供匹配结果的保存、查询、管理功能
 */
/**
 * 业务服务层
 * 职责：事务管理、数据库读写、调用算法引擎
 */
@Service
@Slf4j // 2. 添加注解，自动生成 log 对象
public class MatchingResultService {

    @Autowired
    private MatchingAlgorithmManager algorithmManager;

    @Autowired
    private MajorMapper majorMapper; // 注入 MajorMapper

    @Autowired
    private MatchingResultRepository resultRepository;

    @Autowired
    private SchoolMapper schoolMapper; // 注入 SchoolMapper 用于查询学校详情

    @Autowired
    private CoreCourseRepository coreCourseRepository; // 注入课程Repo

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public List<MatchingResult> performMatching(MatchingRequest request) {
        // 1. 参数基础校验
        validateMatchingRequest(request);

        normalizeStudentInfo(request.getStudentInfo());

        // 2. 调用引擎进行计算 (纯内存操作)
        List<MatchingResult> results = algorithmManager.executeMatching(request);

        // 3. 数据库操作 (事务内)
        Long userId = request.getStudentInfo().getUserId();
        String region = request.getRegion() == null ? null : request.getRegion().trim();
        String studentInfoSnapshot;
        try {
            studentInfoSnapshot = objectMapper.writeValueAsString(request.getStudentInfo());
        } catch (Exception e) {
            log.error("序列化学生信息失败", e);
            studentInfoSnapshot = "{}";
        }

        // 3.1 清理该用户在该区域的历史记录 (避免数据堆积)
        // 如果你想只清理特定区域，可以用 deleteByUserIdAndRegion
        // 这里假设一次全量匹配清理该用户所有旧数据
        resultRepository.deleteByUserIdAndRegion(userId, region);
        resultRepository.flush();

        // 3.2 填充元数据并保存
        Map<Long, School> schoolCache = new HashMap<>();
        Map<Long, List<Major>> majorsCache = new HashMap<>();
        Map<Long, List<CoreCourse>> coreCoursesCache = new HashMap<>();

        for (MatchingResult r : results) {
            r.setStudentInfoSnapshot(studentInfoSnapshot);
            r.setId(null);
            if (r.getRegion() == null || r.getRegion().trim().isEmpty()) {
                r.setRegion(region);
            }

            if (r.getSchoolId() == null) {
                continue;
            }

            School school = schoolCache.computeIfAbsent(r.getSchoolId(), schoolMapper::findById);
            if (school != null) {
                r.setSchoolEnglishName(school.getEnglishName());
                r.setSchoolLogo(school.getLogoUrl());
                r.setSchoolQsRanking(school.getQsRanking());
            }

            if (r.getMajorName() == null || r.getMajorName().trim().isEmpty()) {
                continue;
            }

            List<Major> majors = majorsCache.computeIfAbsent(r.getSchoolId(), majorMapper::findBySchoolId);
            if (majors == null || majors.isEmpty()) {
                continue;
            }

            String targetMajorName = r.getMajorName().trim();
            Major targetMajor = majors.stream()
                    .filter(m -> m.getName() != null && m.getName().trim().equalsIgnoreCase(targetMajorName))
                    .findFirst()
                    .orElse(null);

            if (targetMajor == null) {
                continue;
            }

            r.setMajorDescription(targetMajor.getDescription());
            r.setMajorCurriculum(targetMajor.getCurriculum());
            r.setMajorCareerProspects(targetMajor.getCareerProspects());
            r.setDuration(targetMajor.getDuration());

            if (targetMajor.getTuitionFee() != null) {
                r.setEstimatedTuition(targetMajor.getTuitionFee().toString());
            } else {
                r.setEstimatedTuition("待查询");
            }

            List<CoreCourse> courses = coreCoursesCache.computeIfAbsent(
                    targetMajor.getId(),
                    majorId -> coreCourseRepository
                            .findByMajorIdAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(majorId));
            if (courses != null && !courses.isEmpty()) {
                try {
                    r.setMajorCoreCourses(objectMapper.writeValueAsString(courses));
                } catch (Exception e) {
                    log.error("序列化课程列表失败", e);
                }
            }
        }

        // 批量保存
        List<MatchingResult> savedResults = resultRepository.saveAll(results);

        return savedResults;
    }

    private void normalizeStudentInfo(MatchingRequest.StudentInfoDTO studentInfo) {
        if (studentInfo == null) {
            return;
        }
        if (studentInfo.getGpa() == null) {
            return;
        }

        String scale = studentInfo.getGpaScale();
        if (scale == null || scale.trim().isEmpty()) {
            double gpa = studentInfo.getGpa();
            if (gpa > 5.0) {
                scale = "100";
            } else if (gpa > 4.0) {
                scale = "5.0";
            } else {
                scale = "4.0";
            }
        }

        String normalizedScale = scale.trim();
        double gpa = studentInfo.getGpa();
        if (!"100".equals(normalizedScale)) {
            if ("4".equals(normalizedScale) || "4.0".equals(normalizedScale)) {
                gpa = (gpa / 4.0) * 100.0;
            } else if ("5".equals(normalizedScale) || "5.0".equals(normalizedScale)) {
                gpa = (gpa / 5.0) * 100.0;
            }
        }

        studentInfo.setGpa(Math.max(0.0, Math.min(100.0, gpa)));
        studentInfo.setGpaScale("100");
    }

    /**
     * 根据用户ID获取匹配结果
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页匹配结果
     */
    public Page<MatchingResult> getMatchingResultsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "matchScore"));
        return resultRepository.findByUserId(userId, pageable);
    }

    /**
     * 根据用户ID和地区获取匹配结果
     * 
     * @param userId 用户ID
     * @param region 地区
     * @return 匹配结果列表
     */
    public List<MatchingResult> getMatchingResultsByUserIdAndRegion(Long userId, String region) {
        return resultRepository.findByUserIdAndRegionOrderByMatchScoreDesc(userId, region);
    }

    /**
     * 根据用户ID和匹配等级获取匹配结果
     * 
     * @param userId     用户ID
     * @param matchLevel 匹配等级
     * @return 匹配结果列表
     */
    public List<MatchingResult> getMatchingResultsByUserIdAndMatchLevel(Long userId, String matchLevel) {
        return resultRepository.findByUserIdAndMatchLevelOrderByMatchScoreDesc(userId, matchLevel);
    }

    /**
     * 根据用户ID和分数范围获取匹配结果
     * 
     * @param userId   用户ID
     * @param minScore 最低分数
     * @param maxScore 最高分数
     * @return 匹配结果列表
     */
    public List<MatchingResult> getMatchingResultsByScoreRange(Long userId, Double minScore, Double maxScore) {
        return resultRepository.findByUserIdAndMatchScoreBetweenOrderByMatchScoreDesc(userId, minScore, maxScore);
    }

    /**
     * 获取用户收藏的匹配结果
     * 
     * @param userId 用户ID
     * @return 收藏的匹配结果列表
     */
    public List<MatchingResult> getFavoriteMatchingResults(Long userId) {
        return resultRepository.findByUserIdAndIsFavoriteOrderByMatchScoreDesc(userId, true);
    }

    /**
     * 收藏/取消收藏匹配结果
     * 
     * @param resultId   结果ID
     * @param userId     用户ID
     * @param isFavorite 是否收藏
     * @return 更新后的匹配结果
     */
    public MatchingResult toggleFavorite(Long resultId, Long userId, boolean isFavorite) {
        Optional<MatchingResult> optionalResult = resultRepository.findById(resultId);
        if (optionalResult.isPresent()) {
            MatchingResult result = optionalResult.get();
            // 验证用户权限
            if (!result.getUserId().equals(userId)) {
                throw new IllegalArgumentException("无权限操作此匹配结果");
            }
            result.setIsFavorite(isFavorite);
            result.setUpdatedAt(LocalDateTime.now());
            return resultRepository.save(result);
        } else {
            throw new IllegalArgumentException("匹配结果不存在");
        }
    }

    /**
     * 添加备注
     * 
     * @param resultId 结果ID
     * @param userId   用户ID
     * @param notes    备注内容
     * @return 更新后的匹配结果
     */
    public MatchingResult addNotes(Long resultId, Long userId, String notes) {
        Optional<MatchingResult> optionalResult = resultRepository.findById(resultId);
        if (optionalResult.isPresent()) {
            MatchingResult result = optionalResult.get();
            // 验证用户权限
            if (!result.getUserId().equals(userId)) {
                throw new IllegalArgumentException("无权限操作此匹配结果");
            }
            result.setNotes(notes);
            result.setUpdatedAt(LocalDateTime.now());
            return resultRepository.save(result);
        } else {
            throw new IllegalArgumentException("匹配结果不存在");
        }
    }

    /**
     * 删除用户的匹配结果
     * 
     * @param userId 用户ID
     * @param region 地区（可选，如果指定则只删除该地区的结果）
     */
    public void deleteMatchingResults(Long userId, String region) {
        if (region != null && !region.trim().isEmpty()) {
            resultRepository.deleteByUserIdAndRegion(userId, region);
        } else {
            resultRepository.deleteByUserId(userId);
        }
    }

    /**
     * 删除单个匹配结果
     * 
     * @param resultId 结果ID
     * @param userId   用户ID
     */
    public void deleteMatchingResult(Long resultId, Long userId) {
        Optional<MatchingResult> optionalResult = resultRepository.findById(resultId);
        if (optionalResult.isPresent()) {
            MatchingResult result = optionalResult.get();
            // 验证用户权限
            if (!result.getUserId().equals(userId)) {
                throw new IllegalArgumentException("无权限删除此匹配结果");
            }
            resultRepository.delete(result);
        } else {
            throw new IllegalArgumentException("匹配结果不存在");
        }
    }

    /**
     * 验证匹配请求
     */
    private void validateMatchingRequest(MatchingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("匹配请求不能为空");
        }

        if (request.getRegion() == null || request.getRegion().trim().isEmpty()) {
            throw new IllegalArgumentException("地区不能为空");
        }

        if (request.getStudentInfo() == null) {
            throw new IllegalArgumentException("学生信息不能为空");
        }

        if (request.getStudentInfo().getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
    }

    /**
     * 匹配统计信息
     */
    public static class MatchingStatistics {
        private final long totalCount;
        private final long favoriteCount;
        private final long safeBetCount;
        private final long moderateCount;
        private final long reachCount;
        private final long ukCount;
        private final long usCount;
        private final long australiaCount;
        private final long singaporeCount;
        private final long hongKongCount;

        public MatchingStatistics(long totalCount, long favoriteCount,
                long safeBetCount, long moderateCount, long reachCount,
                long ukCount, long usCount, long australiaCount,
                long singaporeCount, long hongKongCount) {
            this.totalCount = totalCount;
            this.favoriteCount = favoriteCount;
            this.safeBetCount = safeBetCount;
            this.moderateCount = moderateCount;
            this.reachCount = reachCount;
            this.ukCount = ukCount;
            this.usCount = usCount;
            this.australiaCount = australiaCount;
            this.singaporeCount = singaporeCount;
            this.hongKongCount = hongKongCount;
        }

        // Getters
        public long getTotalCount() {
            return totalCount;
        }

        public long getFavoriteCount() {
            return favoriteCount;
        }

        public long getSafeBetCount() {
            return safeBetCount;
        }

        public long getModerateCount() {
            return moderateCount;
        }

        public long getReachCount() {
            return reachCount;
        }

        public long getUkCount() {
            return ukCount;
        }

        public long getUsCount() {
            return usCount;
        }

        public long getAustraliaCount() {
            return australiaCount;
        }

        public long getSingaporeCount() {
            return singaporeCount;
        }

        public long getHongKongCount() {
            return hongKongCount;
        }
    }
}
