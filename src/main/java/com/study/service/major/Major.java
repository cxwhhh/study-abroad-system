package com.study.service.major;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.study.service.faculty.Faculty;
import com.study.service.schools.School;

/**
 * 专业实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "school_programs")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 专业名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 英文名称
     */
    @Column(name = "english_name")
    private String englishName;

    /**
     * 关联学院ID
     */
    @Column(name = "faculty_id")
    private Long facultyId;

    /**
     * 关联院校ID
     */
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * 专业代码
     */
    private String code;

    /**
     * 学位类型
     */
    @Column(name = "degree_type")
    private String degreeType;

    /**
     * 学习时长
     */
    @Column(length = 20)
    private String duration;

    /**
     * 语言
     */
    private String language;

    /**
     * 专业简介
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 课程设置
     */
    @Column(columnDefinition = "TEXT")
    private String curriculum;

    /**
     * 就业方向
     */
    @Column(name = "career_prospects", columnDefinition = "TEXT")
    private String careerProspects;

    /**
     * 学费
     */
    @Column(name = "tuition_fee")
    private BigDecimal tuitionFee;

    /**
     * 奖学金可用
     */
    @Column(name = "scholarship_available")
    private Boolean scholarshipAvailable;

    /**
     * 回扣百分比
     */
    @Column(name = "rebate_percentage")
    private Integer rebatePercentage;

    /**
     * 申请截止日期
     */
    @Column(name = "application_deadline", length = 50)
    private String applicationDeadline;

    /**
     * 开始日期
     */
    @Column(name = "start_date", length = 50)
    private String startDate;

    /**
     * 是否受欢迎
     */
    @Column(name = "is_popular")
    private Boolean isPopular;

    /**
     * 入学要求
     */
    @Column(name = "admission_requirements", columnDefinition = "TEXT")
    private String admissionRequirements;

    /**
     * TOEFL要求
     */
    @Column(name = "toefl_requirement", length = 50)
    private String toeflRequirement;

    /**
     * IELTS要求
     */
    @Column(name = "ielts_requirement")
    private Double ieltsRequirement;

    /**
     * GRE要求
     */
    @Column(name = "gre_requirement", length = 50)
    private String greRequirement;

    /**
     * GMAT要求
     */
    @Column(name = "gmat_requirement", length = 50)
    private String gmatRequirement;

    /**
     * 需要工作经验
     */
    @Column(name = "work_experience_required", length = 50)
    private String workExperienceRequired;

    /**
     * 学分小时数
     */
    @Column(name = "credit_hours")
    private Integer creditHours;

    /**
     * 需要论文
     */
    @Column(name = "thesis_required")
    private Boolean thesisRequired;

    /**
     * 可实习
     */
    @Column(name = "internship_available")
    private Boolean internshipAvailable;

    /**
     * 专业网站
     */
    private String website;

    /**
     * 小册子URL
     */
    @Column(name = "brochure_url")
    private String brochureUrl;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 是否已删除
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 关联学院
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", insertable = false, updatable = false)
    private Faculty faculty;
    
    /**
     * 关联院校
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;
    
    /**
     * 核心课程列表
     */
    @OneToMany(mappedBy = "major", fetch = FetchType.LAZY)
    private List<CoreCourse> coreCourses;
}
