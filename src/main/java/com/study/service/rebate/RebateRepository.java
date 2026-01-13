package com.study.service.rebate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 返点申请数据访问层
 */
@Repository
public interface RebateRepository extends JpaRepository<Rebate, Long> {

    /**
     * 根据入学记录ID查找返点申请
     */
    List<Rebate> findByEnrollmentId(Long enrollmentId);

    /**
     * 根据状态查找返点申请
     */
    List<Rebate> findByStatus(String status);

    /**
     * 根据审核状态查找返点申请
     */
    List<Rebate> findByReviewStatus(String reviewStatus);

    /**
     * 根据支付状态查找返点申请
     */
    List<Rebate> findByPaymentStatus(String paymentStatus);

    /**
     * 查找指定入学记录的最新返点申请
     */
    @Query("SELECT r FROM Rebate r WHERE r.enrollmentId = :enrollmentId ORDER BY r.createTime DESC")
    List<Rebate> findByEnrollmentIdOrderByCreateTimeDesc(@Param("enrollmentId") Long enrollmentId);

    /**
     * 统计各状态的返点申请数量
     */
    long countByStatus(String status);

    /**
     * 统计各审核状态的返点申请数量
     */
    long countByReviewStatus(String reviewStatus);

    /**
     * 计算指定状态的返点总金额
     */
    @Query("SELECT SUM(r.rebateAmount) FROM Rebate r WHERE r.status = :status")
    Double sumRebateAmountByStatus(@Param("status") String status);
}
