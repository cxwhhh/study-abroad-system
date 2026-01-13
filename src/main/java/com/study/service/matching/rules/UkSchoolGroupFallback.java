package com.study.service.matching.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 英国目标学校的“分组兜底策略”（按 target_school_id 维度）
 *
 * 用途：当 uk_school_undergrad_group 没有为某个中国本科院校配置显式映射时，
 * 通过该表为“该英国学校”提供可控的默认分组策略，避免必须一次性配齐所有中国学校。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UkSchoolGroupFallback {
    /**
     * 英国目标学校ID（schools.id）
     */
    private Long targetSchoolId;
    /**
     * 兜底策略类型
     *
     * - DEFAULT_GROUP：未配置映射时直接使用 defaultGroupCode
     * - TAG_BASED：根据 undergrad_schools 的标签（985/211/双一流/学校类型）映射到该校的分组
     */
    private String fallbackStrategy;
    /**
     * 默认分组（最保守兜底）
     */
    private String defaultGroupCode;
    /**
     * 985 对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCode985;
    /**
     * 211 对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCode211;
    /**
     * 双一流对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCodeDoubleFirstClass;
    /**
     * 其它公办本科对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCodeOtherPublic;
    /**
     * 民办对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCodePrivate;
    /**
     * 独立学院对应的分组（仅 TAG_BASED 生效）
     */
    private String groupCodeIndependent;
    private Date createdAt;
    private Date updatedAt;
}
