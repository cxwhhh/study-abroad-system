package com.study.service.faculty;

import java.util.Date;
import java.util.List;

import com.study.service.major.Major;
import com.study.service.schools.School;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "school_faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学院名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 英文名称
     */
    @Column(name = "english_name")
    private String englishName;

    /**
     * 关联院校ID
     */
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    /**
     * 学院代码
     */
    private String code;

    /**
     * 学院简介
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 学院历史
     */
    @Column(columnDefinition = "TEXT")
    private String history;

    /**
     * 学院排名
     */
    private Integer ranking;

    /**
     * 院长姓名
     */
    @Column(name = "dean_name")
    private String deanName;

    /**
     * 教师数量
     */
    @Column(name = "faculty_count")
    private Integer facultyCount;

    /**
     * 学生数量
     */
    @Column(name = "student_count")
    private Integer studentCount;

    /**
     * 学院网站
     */
    private String website;

    /**
     * 学院图片URL
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 特色专业
     */
    @Column(name = "featured_programs", columnDefinition = "TEXT")
    private String featuredPrograms;

    /**
     * 研究领域
     */
    @Column(name = "research_areas", columnDefinition = "TEXT")
    private String researchAreas;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email")
    private String contactEmail;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone")
    private String contactPhone;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", updatable = false)
    private Date createdTime;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_time")
    private Date updatedTime;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 关联院校
     */
    @ManyToOne
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;
    
    /**
     * 关联专业列表 (非持久化字段，用于传输数据)
     */
    @Transient
    private List<Major> majors;
} 