package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import java.util.List;

/**
 * 【核心接口】学校匹配策略接口
 * 所有具体的学校算法（如杜伦、伯明翰）都必须实现此接口
 */
public interface SchoolMatchingStrategy {

    /**
     * 获取该算法对应的学校名称（用于区分）
     */
    String getSchoolName();

    /**
     * 获取所属地区 (用于地区筛选，如：英国、美国)
     */
    String getRegion();

    /**
     * 执行匹配逻辑
     * 
     * @param request 学生请求信息
     * @return 匹配结果列表（通常针对该学校的不同专业）
     */
    List<MatchingResult> computeMatchingResults(MatchingRequest request);

    /**
     * 判断该策略是否支持当前请求 (可选，用于更复杂的过滤)
     */
    default boolean supports(MatchingRequest request) {
        // 默认逻辑：如果请求指定了学校列表，必须包含当前学校；否则按地区匹配
        if (request.getTargetSchools() != null && !request.getTargetSchools().isEmpty()) {
            return request.getTargetSchools().contains(getSchoolName());
        }
        return request.getRegion() != null && request.getRegion().equals(getRegion());
    }
}
