package com.study.service.matching.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 中国本科院校标准化（全局维度）
 *
 * 目标：把各种“学校写法”（中文/英文/简称/别名）归一到一个 undergrad_school_id（或 canonical_key），
 * 供后续“按英国目标学校维度”的分组判定与门槛查询使用。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndergradSchool {
    /**
     * 标准化学校ID（全局唯一）
     */
    private Long id;
    /**
     * 标准中文名（canonical）
     */
    private String canonicalName;
    /**
     * 标准英文名（canonical，可选）
     */
    private String canonicalEnglishName;
    /**
     * 标准化键（用于去重/导入对齐）
     */
    private String canonicalKey;
    /**
     * 是否 985（用于每校兜底分组策略）
     */
    private Boolean is985;
    /**
     * 是否 211（用于每校兜底分组策略）
     */
    private Boolean is211;
    /**
     * 是否双一流（用于每校兜底分组策略）
     */
    private Boolean isDoubleFirstClass;
    /**
     * 学校类型（用于每校兜底分组策略）
     *
     * 建议值：public/private/independent（或对齐中文：公办/民办/独立学院）
     */
    private String schoolType;
    private Date createdAt;
    private Date updatedAt;
}
