package com.study.service.enrollment;

import java.util.Date;

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

import com.study.service.user.User;
import com.study.service.schools.School;
import com.study.service.application.Application;

/**
 * 入学信息实体类
 * 用于记录学生的入学信息，方便管理返点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 申请ID
     */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    /**
     * 学生ID
     */
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 学校ID
     */
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    /**
     * 专业ID
     */
    @Column(name = "program_id")
    private Long programId;

    /**
     * 入学时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enrollment_date", nullable = false)
    private Date enrollmentDate;

    /**
     * 学期 (Fall, Spring, Summer)
     */
    @Column(name = "semester", length = 20)
    private String semester;

    /**
     * 学年
     */
    @Column(name = "academic_year")
    private Integer academicYear;

    /**
     * 学费金额
     */
    @Column(name = "tuition_fee")
    private Double tuitionFee;

    /**
     * 返点金额
     */
    @Column(name = "commission_amount")
    private Double commissionAmount;

    /**
     * 返点状态 (pending, paid, cancelled)
     */
    @Column(name = "commission_status", length = 20, nullable = false)
    private String commissionStatus = "pending";

    /**
     * 返点支付时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "commission_paid_date")
    private Date commissionPaidDate;

    /**
     * 备注
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
     * 创建者ID（管理员ID）
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 关联申请
     */
    @ManyToOne
    @JoinColumn(name = "application_id", insertable = false, updatable = false)
    private Application application;

    /**
     * 关联学生
     */
    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    /**
     * 关联学校
     */
    @ManyToOne
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;
}