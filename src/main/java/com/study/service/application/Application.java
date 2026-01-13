package com.study.service.application;

import java.util.Date;

import com.study.service.major.Major;
import com.study.service.schools.School;
import com.study.service.user.User;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 申请实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 院校ID
     */
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    /**
     * 专业ID
     */
    @Column(name = "major_id")
    private Long majorId;

    /**
     * 申请类型 (本科、硕士、博士)
     */
    @Column(name = "application_type", length = 20)
    private String applicationType;

    /**
     * 申请状态 (草稿、已提交、已审核、已录取、已拒绝)
     */
    @Column(length = 20)
    private String status;

    /**
     * 申请季 (秋季、春季)
     */
    @Column(name = "application_season", length = 20)
    private String applicationSeason;

    /**
     * 申请年份
     */
    @Column(name = "application_year")
    private Integer applicationYear;

    /**
     * 申请备注
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

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
    
    /**
     * 关联用户
     */
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    /**
     * 关联院校
     */
    @ManyToOne
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;
    
    /**
     * 关联专业
     */
    @ManyToOne
    @JoinColumn(name = "major_id", insertable = false, updatable = false)
    private Major major;
} 