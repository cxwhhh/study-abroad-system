package com.study.service.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 入学信息数据访问层
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * 根据申请ID查询入学信息
     */
    Optional<Enrollment> findByApplicationId(Long applicationId);

    /**
     * 根据学生ID查询入学信息列表
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * 根据学校ID查询入学信息列表
     */
    List<Enrollment> findBySchoolId(Long schoolId);

    /**
     * 根据返点状态查询入学信息
     */
    List<Enrollment> findByCommissionStatus(String commissionStatus);

    /**
     * 分页查询返点状态的入学信息
     */
    Page<Enrollment> findByCommissionStatus(String commissionStatus, Pageable pageable);

    /**
     * 根据学年查询入学信息
     */
    List<Enrollment> findByAcademicYear(Integer academicYear);

    /**
     * 根据入学时间范围查询入学信息
     */
    List<Enrollment> findByEnrollmentDateBetween(Date startDate, Date endDate);

    /**
     * 查询指定时间范围内的返点总金额
     */
    @Query("SELECT SUM(e.commissionAmount) FROM Enrollment e WHERE e.commissionStatus = :status AND e.enrollmentDate BETWEEN :startDate AND :endDate")
    Double sumCommissionAmountByStatusAndDateRange(@Param("status") String status, 
                                                   @Param("startDate") Date startDate, 
                                                   @Param("endDate") Date endDate);

    /**
     * 统计指定状态的入学记录数量
     */
    long countByCommissionStatus(String commissionStatus);

    /**
     * 检查申请是否已经有入学记录
     */
    boolean existsByApplicationId(Long applicationId);

    /**
     * 根据学生ID和学校ID查询入学信息
     */
    List<Enrollment> findByStudentIdAndSchoolId(Long studentId, Long schoolId);

    /**
     * 查询待支付返点的入学信息
     */
    @Query("SELECT e FROM Enrollment e WHERE e.commissionStatus = 'pending' AND e.commissionAmount > 0")
    List<Enrollment> findPendingCommissions();

    /**
     * 根据创建者查询入学信息
     */
    List<Enrollment> findByCreatedBy(Long createdBy);

    /**
     * 复杂查询：根据多个条件查询入学信息
     */
    @Query("SELECT e FROM Enrollment e WHERE " +
           "(:studentId IS NULL OR e.studentId = :studentId) AND " +
           "(:schoolId IS NULL OR e.schoolId = :schoolId) AND " +
           "(:commissionStatus IS NULL OR e.commissionStatus = :commissionStatus) AND " +
           "(:academicYear IS NULL OR e.academicYear = :academicYear) AND " +
           "(:startDate IS NULL OR e.enrollmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.enrollmentDate <= :endDate)")
    Page<Enrollment> findByMultipleConditions(@Param("studentId") Long studentId,
                                              @Param("schoolId") Long schoolId,
                                              @Param("commissionStatus") String commissionStatus,
                                              @Param("academicYear") Integer academicYear,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              Pageable pageable);
}