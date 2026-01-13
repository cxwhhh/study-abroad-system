package com.study.service.major;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 核心课程实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "major_core_courses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "major"})
public class CoreCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联专业ID
     */
    @Column(name = "major_id", nullable = false)
    private Long majorId;

    /**
     * 课程名称
     */
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    /**
     * 课程英文名称
     */
    @Column(name = "course_english_name", length = 200)
    private String courseEnglishName;

    /**
     * 课程代码
     */
    @Column(name = "course_code", length = 50)
    private String courseCode;

    /**
     * 课程描述
     */
    @Column(name = "course_description", columnDefinition = "TEXT")
    private String courseDescription;

    /**
     * 学分
     */
    @Column(name = "credits")
    private Integer credits;

    /**
     * 课程类型 (必修/选修)
     */
    @Column(name = "course_type", length = 20)
    private String courseType;

    /**
     * 学期 (第几学期)
     */
    @Column(name = "semester")
    private Integer semester;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @Column(name = "is_active")
    private Boolean isActive;

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
     * 关联专业
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", insertable = false, updatable = false)
    private Major major;
}