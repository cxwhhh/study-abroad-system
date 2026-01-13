package com.study.service.rebate;

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

import com.study.service.enrollment.Enrollment;

/**
 * 返点申请实体类
 * 用于记录学生的返点申请信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rebates")
public class Rebate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 入学记录ID
     */
    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    /**
     * 返点金额
     */
    @Column(name = "rebate_amount", nullable = false)
    private Double rebateAmount;

    /**
     * 货币类型
     */
    @Column(name = "currency", length = 10)
    private String currency = "CNY";

    /**
     * 返点类型 (tuition_rebate, accommodation_rebate, living_allowance)
     */
    @Column(name = "rebate_type", length = 50)
    private String rebateType;

    /**
     * 申请状态 (pending, approved, rejected, paid)
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";

    /**
     * 申请日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date", nullable = false)
    private Date requestDate;

    /**
     * 处理日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "processed_date")
    private Date processedDate;

    /**
     * 支付方式 (bank_transfer, alipay, wechat_pay)
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * 银行账号
     */
    @Column(name = "account_number", length = 100)
    private String accountNumber;

    /**
     * 数字账户 (支付宝/微信账号)
     */
    @Column(name = "digital_account", length = 100)
    private String digitalAccount;

    /**
     * 证明文件路径
     */
    @Column(name = "proof_file_path", length = 500)
    private String proofFilePath;

    /**
     * 是否同意协议
     */
    @Column(name = "agreement_accepted", nullable = false)
    private Boolean agreementAccepted = false;

    /**
     * 协议同意日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "agreement_date")
    private Date agreementDate;

    /**
     * 审核状态 (pending, approved, rejected)
     */
    @Column(name = "review_status", length = 20)
    private String reviewStatus = "pending";

    /**
     * 审核时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "review_time")
    private Date reviewTime;

    /**
     * 审核人ID
     */
    @Column(name = "reviewer_id")
    private Long reviewerId;

    /**
     * 审核备注
     */
    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    /**
     * 支付状态 (pending, processing, completed, failed)
     */
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "pending";

    /**
     * 支付时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_time")
    private Date paymentTime;

    /**
     * 支付凭证
     */
    @Column(name = "payment_proof", length = 500)
    private String paymentProof;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 关联的入学记录
     */
    @ManyToOne
    @JoinColumn(name = "enrollment_id", insertable = false, updatable = false)
    private Enrollment enrollment;
}