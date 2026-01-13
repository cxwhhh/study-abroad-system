package com.study.service.enrollment;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入学信息服务类
 */
@Service
@Transactional
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * 创建入学记录
     */
    public Enrollment createEnrollment(Long applicationId, Long studentId, Long schoolId, 
                                     Long programId, Date enrollmentDate, Long createdBy) {
        
        // 检查是否已存在入学记录
        if (enrollmentRepository.existsByApplicationId(applicationId)) {
            throw new RuntimeException("该申请已经有入学记录");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setApplicationId(applicationId);
        enrollment.setStudentId(studentId);
        enrollment.setSchoolId(schoolId);
        enrollment.setProgramId(programId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setCreatedBy(createdBy);
        
        // 设置学期和学年
        Calendar cal = Calendar.getInstance();
        cal.setTime(enrollmentDate);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int year = cal.get(Calendar.YEAR);
        
        // 根据月份判断学期
        if (month >= 8 && month <= 12) {
            enrollment.setSemester("Fall");
            enrollment.setAcademicYear(year);
        } else if (month >= 1 && month <= 5) {
            enrollment.setSemester("Spring");
            enrollment.setAcademicYear(year);
        } else {
            enrollment.setSemester("Summer");
            enrollment.setAcademicYear(year);
        }
        
        // 设置默认返点状态
        enrollment.setCommissionStatus("pending");
        
        // 设置创建和更新时间
        Date now = new Date();
        enrollment.setCreatedAt(now);
        enrollment.setUpdatedAt(now);
        
        return enrollmentRepository.save(enrollment);
    }

    /**
     * 检查申请是否已有入学记录
     */
    public boolean existsByApplicationId(Long applicationId) {
        return enrollmentRepository.existsByApplicationId(applicationId);
    }

    /**
     * 根据申请ID获取入学记录
     */
    public Optional<Enrollment> getEnrollmentByApplicationId(Long applicationId) {
        return enrollmentRepository.findByApplicationId(applicationId);
    }

    /**
     * 分页查询入学记录
     */
    public Page<Enrollment> getEnrollments(Long studentId, Long schoolId, String commissionStatus,
                                         Integer academicYear, Date startDate, Date endDate,
                                         Pageable pageable) {
        return enrollmentRepository.findByMultipleConditions(
            studentId, schoolId, commissionStatus, academicYear, startDate, endDate, pageable);
    }

    /**
     * 根据学生ID获取入学记录
     */
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    /**
     * 根据学校ID获取入学记录
     */
    public List<Enrollment> getEnrollmentsBySchoolId(Long schoolId) {
        return enrollmentRepository.findBySchoolId(schoolId);
    }

    /**
     * 更新返点状态
     */
    public Enrollment updateCommissionStatus(Long enrollmentId, String commissionStatus, Double commissionAmount) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(enrollmentId);
        if (!optionalEnrollment.isPresent()) {
            throw new RuntimeException("入学记录不存在");
        }
        
        Enrollment enrollment = optionalEnrollment.get();
        enrollment.setCommissionStatus(commissionStatus);
        
        if (commissionAmount != null) {
            enrollment.setCommissionAmount(commissionAmount);
        }
        
        // 如果状态改为已支付，设置支付时间
        if ("paid".equals(commissionStatus)) {
            enrollment.setCommissionPaidDate(new Date());
        }
        
        enrollment.setUpdatedAt(new Date());
        
        return enrollmentRepository.save(enrollment);
    }

    /**
     * 获取返点统计信息
     */
    public Map<String, Object> getCommissionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态的记录数量
        long pendingCount = enrollmentRepository.countByCommissionStatus("pending");
        long paidCount = enrollmentRepository.countByCommissionStatus("paid");
        long cancelledCount = enrollmentRepository.countByCommissionStatus("cancelled");
        
        statistics.put("pendingCount", pendingCount);
        statistics.put("paidCount", paidCount);
        statistics.put("cancelledCount", cancelledCount);
        statistics.put("totalCount", pendingCount + paidCount + cancelledCount);
        
        // 计算本年度的返点金额
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 0); // January
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date yearStart = cal.getTime();
        
        cal.add(Calendar.YEAR, 1);
        Date yearEnd = cal.getTime();
        
        Double totalPaidAmount = enrollmentRepository.sumCommissionAmountByStatusAndDateRange(
            "paid", yearStart, yearEnd);
        Double totalPendingAmount = enrollmentRepository.sumCommissionAmountByStatusAndDateRange(
            "pending", yearStart, yearEnd);
        
        statistics.put("totalPaidAmount", totalPaidAmount != null ? totalPaidAmount : 0.0);
        statistics.put("totalPendingAmount", totalPendingAmount != null ? totalPendingAmount : 0.0);
        
        return statistics;
    }

    /**
     * 获取待支付返点的入学记录
     */
    public List<Enrollment> getPendingCommissions() {
        return enrollmentRepository.findPendingCommissions();
    }

    /**
     * 根据返点状态获取入学记录
     */
    public List<Enrollment> getEnrollmentsByCommissionStatus(String commissionStatus) {
        return enrollmentRepository.findByCommissionStatus(commissionStatus);
    }

    /**
     * 根据学年获取入学记录
     */
    public List<Enrollment> getEnrollmentsByAcademicYear(Integer academicYear) {
        return enrollmentRepository.findByAcademicYear(academicYear);
    }

    /**
     * 根据时间范围获取入学记录
     */
    public List<Enrollment> getEnrollmentsByDateRange(Date startDate, Date endDate) {
        return enrollmentRepository.findByEnrollmentDateBetween(startDate, endDate);
    }

    /**
     * 删除入学记录
     */
    public void deleteEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new RuntimeException("入学记录不存在");
        }
        enrollmentRepository.deleteById(enrollmentId);
    }

    /**
     * 根据ID获取入学记录
     */
    public Optional<Enrollment> getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }
}
