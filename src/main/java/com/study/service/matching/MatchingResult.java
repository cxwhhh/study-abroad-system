package com.study.service.matching;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 匹配结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matching_results")
public class MatchingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 学校ID
     */
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    /**
     * 学校名称（冗余字段，便于查询）
     */
    @Column(name = "school_name", nullable = false)
    private String schoolName;

    // --- 新增字段 Start ---
    /**
     * 学校英文名 (快照)
     */
    @Column(name = "school_english_name")
    private String schoolEnglishName;

    /**
     * 学校Logo URL (快照)
     */
    @Column(name = "school_logo")
    private String schoolLogo;

    /**
     * QS排名 (快照)
     */
    @Column(name = "school_qs_ranking")
    private Integer schoolQsRanking;

    /**
     * 专业名称
     */
    @Column(name = "major_name")
    private String majorName;

    // 【新增】添加专业英文名字段
    @Column(name = "major_english_name")
    private String majorEnglishName;
    /**
     * 
     * 匹配分数
     */
    @Column(name = "match_score", nullable = false)
    private Double matchScore;

    /**
     * 匹配等级（如：冲刺、适中、保底）
     */
    @Column(name = "match_level", nullable = false)
    private String matchLevel;

    /**
     * 匹配理由
     */
    @Column(name = "match_reason", columnDefinition = "TEXT")
    private String matchReason;

    /**
     * 专业描述 (从Major表同步)
     */
    @Column(name = "major_description", columnDefinition = "TEXT")
    private String majorDescription;

    /**
     * 核心课程 (对应 Major.curriculum)
     */
    @Column(name = "major_curriculum", columnDefinition = "TEXT")
    private String majorCurriculum;

    /**
     * 核心课程快照 (JSON字符串，存储 List<CoreCourse>)
     */
    @Column(name = "major_core_courses", columnDefinition = "TEXT")
    private String majorCoreCourses;

    /**
     * 就业前景 (对应 Major.careerProspects)
     */
    @Column(name = "major_career_prospects", columnDefinition = "TEXT")
    private String majorCareerProspects;


    /**
     * 课程设置 (从Major表同步)
     */
    @Column(name = "curriculum", columnDefinition = "TEXT")
    private String curriculum;


    /**
     * 就业前景 (从Major表同步)
     */
    @Column(name = "career_prospects", columnDefinition = "TEXT")
    private String careerProspects;

    /**
     * 学费 (对应 Major.tuitionFee)
     * 为了方便展示，建议存字符串，或者你在前端格式化
     */
    @Column(name = "estimated_tuition")
    private String estimatedTuition;

    /**
     * 学制 (对应 Major.duration)
     */
    @Column(name = "duration")
    private String duration;

    /**
     * 录取概率
     */
    @Column(name = "admission_probability")
    private Double admissionProbability;

    /**
     * 推荐指数
     */
    @Column(name = "recommendation_index")
    private Double recommendationIndex;

    /**
     * 地区
     */
    @Column(name = "region")
    private String region;

    /**
     * 使用的算法策略
     */
    @Column(name = "algorithm_strategy")
    private String algorithmStrategy;

    /**
     * 风险分析/匹配详情说明
     * 用于告诉前端用户具体的GPA差距或拒信原因
     */
    @Column(name = "risk_analysis")
    private String riskAnalysis;

    /**
     * 学生信息快照（JSON格式）
     */
    @Column(name = "student_info_snapshot", columnDefinition = "TEXT")
    private String studentInfoSnapshot;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否收藏
     */
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
