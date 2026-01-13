package com.study.service.schools;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 院校实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schools")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 院校名称
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * 英文名称
     */
    @Column(name = "english_name", length = 255)
    private String englishName;

    /**
     * 国家
     */
    @Column(nullable = false, length = 50)
    private String country;
    
    /**
     * 国家代码
     */
    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    /**
     * 城市
     */
    @Column(length = 100)
    private String city;
    
    /**
     * 所在州/省
     */
    @Column(length = 100)
    private String state;
    
    /**
     * 详细地址
     */
    @Column(length = 255)
    private String location;
    
    /**
     * 校区信息
     */
    @Column(length = 255)
    private String campus;

    /**
     * 院校类型
     */
    @Column(length = 50)
    private String type;
    
    /**
     * 公立/私立
     */
    @Column(length = 50)
    private String ownership;
    
    /**
     * 院校介绍
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 学校历史
     */
    @Column(columnDefinition = "TEXT")
    private String history;
    
    /**
     * 学校特色
     */
    @Column(columnDefinition = "TEXT")
    private String features;
    
    /**
     * 世界排名
     */
    @Column(name = "world_ranking")
    private Integer worldRanking;
    
    /**
     * 本国排名
     */
    @Column(name = "national_ranking")
    private Integer nationalRanking;

    /**
     * QS世界排名
     */
    @Column(name = "qs_ranking")
    private Integer qsRanking;

    /**
     * THE泰晤士排名
     */
    @Column(name = "times_ranking")
    private Integer timesRanking;
    
    /**
     * 上海交大排名
     */
    @Column(name = "shanghai_ranking")
    private Integer shanghaiRanking;

    /**
     * 学费
     */
    @Column(name = "tuition_fee", precision = 10, scale = 2)
    private BigDecimal tuitionFee;
    
    /**
     * 返点比例(%)
     */
    @Column(name = "rebate_percentage", precision = 5, scale = 2)
    private BigDecimal rebatePercentage;
    
    /**
     * 生活费估算(人民币/年)
     */
    @Column(name = "living_cost", precision = 12, scale = 2)
    private BigDecimal livingCost;
    
    /**
     * 货币单位
     */
    @Column(length = 10)
    private String currency;

    /**
     * 学费币种
     */
    @Column(name = "fee_currency", length = 10)
    private String feeCurrency;
    
    /**
     * 国际学生比例(%)
     */
    @Column(name = "international_student_percentage", precision = 5, scale = 2)
    private BigDecimal internationalStudentPercentage;
    
    /**
     * 中国学生数量
     */
    @Column(name = "chinese_student_count")
    private Integer chineseStudentCount;
    
    /**
     * 学生总数
     */
    @Column(name = "total_student_count")
    private Integer totalStudentCount;
    
    /**
     * 师生比例
     */
    @Column(name = "student_faculty_ratio", length = 20)
    private String studentFacultyRatio;
    
    /**
     * 录取率(%)
     */
    @Column(name = "acceptance_rate", precision = 5, scale = 2)
    private BigDecimal acceptanceRate;
    
    /**
     * 平均录取GPA
     */
    @Column(name = "average_gpa", precision = 3, scale = 2)
    private BigDecimal averageGpa;
    
    /**
     * TOEFL要求
     */
    @Column(name = "toefl_requirement", length = 50)
    private String toeflRequirement;
    
    /**
     * IELTS要求
     */
    @Column(name = "ielts_requirement", length = 50)
    private String ieltsRequirement;
    
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
     * SAT要求
     */
    @Column(name = "sat_requirement", length = 50)
    private String satRequirement;
    
    /**
     * 申请截止日期
     */
    @Column(name = "application_deadline", length = 100)
    private String applicationDeadline;

    /**
     * 官网地址
     */
    @Column(length = 255)
    private String website;

    /**
     * 院校LOGO URL
     */
    @Column(name = "logo_url", length = 255)
    private String logoUrl;
    
    /**
     * 学校图片URL
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    /**
     * 横幅图片URL
     */
    @Column(name = "banner_url", length = 255)
    private String bannerUrl;
    
    /**
     * 是否提供本科教育
     */
    @Column(name = "has_undergraduate")
    private Boolean hasUndergraduate;
    
    /**
     * 是否提供研究生教育
     */
    @Column(name = "has_graduate")
    private Boolean hasGraduate;
    
    /**
     * 是否提供博士教育
     */
    @Column(name = "has_phd")
    private Boolean hasPhd;

    /**
     * 院校标签，以逗号分隔
     */
    @Column(length = 255)
    private String tags;
    
    /**
     * 学生服务
     */
    @Column(name = "student_services", columnDefinition = "TEXT")
    private String studentServices;
    
    /**
     * 住宿信息
     */
    @Column(name = "housing_info", columnDefinition = "TEXT")
    private String housingInfo;
    
    /**
     * 著名校友
     */
    @Column(name = "famous_alumni", columnDefinition = "TEXT")
    private String famousAlumni;
    
    /**
     * 知名专业
     */
    @Column(name = "notable_programs", columnDefinition = "TEXT")
    private String notablePrograms;
    
    /**
     * 入学要求
     */
    @Column(name = "entry_requirement", columnDefinition = "TEXT")
    private String entryRequirement;
    
    /**
     * 奖学金信息
     */
    @Column(name = "scholarship_info", columnDefinition = "TEXT")
    private String scholarshipInfo;
    
    /**
     * 签证信息
     */
    @Column(name = "visa_info", columnDefinition = "TEXT")
    private String visaInfo;
    
    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    /**
     * 特色专业
     */
    @Column(name = "featured_programs", columnDefinition = "TEXT")
    private String featuredPrograms;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private Date updatedTime;
    
    /**
     * 是否删除(0-未删除，1-已删除)
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    
    /**
     * 状态(0-禁用，1-启用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
} 