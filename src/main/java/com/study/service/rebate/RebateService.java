package com.study.service.rebate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.service.enrollment.Enrollment;
import com.study.service.enrollment.EnrollmentRepository;
import com.study.service.applicationschool.ApplicationSchool;
import com.study.service.applicationschool.ApplicationSchoolMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * 返点申请服务类
 */
@Service
@Transactional
public class RebateService {

    @Autowired
    private RebateRepository rebateRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ApplicationSchoolMapper applicationSchoolMapper;

    /**
     * 验证学生申请ID并获取入学信息
     */
    public Map<String, Object> validateStudentApplication(Long applicationId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 查找入学记录
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByApplicationId(applicationId);
            
            if (!enrollmentOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "未找到该申请ID对应的入学记录，请确认申请ID是否正确");
                return result;
            }
            
            Enrollment enrollment = enrollmentOpt.get();
            
            // 2. 检查是否已有返点申请
            List<Rebate> existingRebates = rebateRepository.findByEnrollmentId(enrollment.getId());
            if (!existingRebates.isEmpty()) {
                result.put("success", false);
                result.put("message", "该申请ID已经提交过返点申请");
                result.put("existingRebate", existingRebates.get(0));
                return result;
            }
            
            // 3. 获取申请学校信息
            // 注意：enrollment.getApplicationId() 存储的是 application_schools 表的 id
            ApplicationSchool applicationSchool = applicationSchoolMapper.findDetailById(enrollment.getApplicationId());
            
            result.put("success", true);
            result.put("enrollment", enrollment);
            result.put("applicationSchool", applicationSchool);
            result.put("message", "验证成功，请确认申请信息");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证过程中发生错误: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 验证学校和专业信息
     */
    public Map<String, Object> validateSchoolAndMajor(Long applicationId, String schoolName, String majorName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 先查找入学记录
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByApplicationId(applicationId);
            
            if (!enrollmentOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "未找到该申请ID对应的入学记录");
                return result;
            }
            
            Enrollment enrollment = enrollmentOpt.get();
            
            // 2. 查找申请学校记录
            // enrollment.getApplicationId() 存储的是 application_schools 表的 id
            ApplicationSchool applicationSchool = applicationSchoolMapper.findDetailById(enrollment.getApplicationId());
            
            if (applicationSchool == null) {
                result.put("success", false);
                result.put("message", "未找到对应的申请学校记录");
                return result;
            }
            
            // 3. 检查学校名称和专业名称是否匹配（使用包含匹配，更灵活）
            boolean schoolMatches = applicationSchool.getSchoolName() != null && 
                                  (applicationSchool.getSchoolName().toLowerCase().contains(schoolName.toLowerCase()) ||
                                   schoolName.toLowerCase().contains(applicationSchool.getSchoolName().toLowerCase()));
            boolean majorMatches = applicationSchool.getMajorName() != null &&
                                 (applicationSchool.getMajorName().toLowerCase().contains(majorName.toLowerCase()) ||
                                  majorName.toLowerCase().contains(applicationSchool.getMajorName().toLowerCase()));
            
            if (!schoolMatches || !majorMatches) {
                result.put("success", false);
                result.put("message", "提供的学校或专业信息与申请记录不匹配，请重新确认");
                result.put("expectedSchool", applicationSchool.getSchoolName());
                result.put("expectedMajor", applicationSchool.getMajorName());
                result.put("providedSchool", schoolName);
                result.put("providedMajor", majorName);
                return result;
            }
            
            result.put("success", true);
            result.put("enrollment", enrollment);
            result.put("applicationSchool", applicationSchool);
            result.put("message", "申请信息验证成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证过程中发生错误: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 提交返点申请
     */
    public Map<String, Object> submitRebateApplication(RebateApplicationRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 再次验证入学记录
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByApplicationId(request.getApplicationId());
            if (!enrollmentOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "入学记录不存在");
                return result;
            }
            
            Enrollment enrollment = enrollmentOpt.get();
            
            // 2. 计算返点金额 (基于学费的一定比例)
            Double rebateAmount = calculateRebateAmount(enrollment);
            
            // 3. 创建返点申请记录
            Rebate rebate = new Rebate();
            rebate.setEnrollmentId(enrollment.getId());
            rebate.setRebateAmount(rebateAmount);
            rebate.setCurrency("CNY");
            rebate.setRebateType("tuition_rebate");
            rebate.setStatus("pending");
            rebate.setRequestDate(new Date());
            rebate.setPaymentMethod(request.getPaymentMethod());
            rebate.setAccountNumber(request.getAccountNumber());
            rebate.setDigitalAccount(request.getDigitalAccount());
            rebate.setProofFilePath(request.getProofFilePath());
            rebate.setAgreementAccepted(request.getAgreementAccepted());
            rebate.setAgreementDate(new Date());
            rebate.setReviewStatus("pending");
            rebate.setPaymentStatus("pending");
            rebate.setNotes(request.getNotes());
            rebate.setCreateTime(new Date());
            rebate.setUpdateTime(new Date());
            
            // 4. 保存返点申请
            Rebate savedRebate = rebateRepository.save(rebate);
            
            result.put("success", true);
            result.put("rebate", savedRebate);
            result.put("message", "返点申请提交成功，我们将在1-2个工作日内审核您的申请");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提交申请时发生错误: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算返点金额
     */
    private Double calculateRebateAmount(Enrollment enrollment) {
        if (enrollment.getTuitionFee() == null) {
            return 0.0;
        }
        
        // 基础返点比例为5%，可以根据学校、专业等因素调整
        double rebateRate = 0.05;
        
        // 根据学费金额调整返点比例
        if (enrollment.getTuitionFee() > 100000) {
            rebateRate = 0.08; // 学费超过10万，返点8%
        } else if (enrollment.getTuitionFee() > 50000) {
            rebateRate = 0.06; // 学费超过5万，返点6%
        }
        
        return enrollment.getTuitionFee() * rebateRate;
    }

    /**
     * 获取返点申请详情
     */
    public Optional<Rebate> getRebateById(Long id) {
        return rebateRepository.findById(id);
    }

    /**
     * 获取指定入学记录的返点申请列表
     */
    public List<Rebate> getRebatesByEnrollmentId(Long enrollmentId) {
        return rebateRepository.findByEnrollmentId(enrollmentId);
    }

    /**
     * 获取返点申请统计信息
     */
    public Map<String, Object> getRebateStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态的申请数量
        long pendingCount = rebateRepository.countByStatus("pending");
        long approvedCount = rebateRepository.countByStatus("approved");
        long rejectedCount = rebateRepository.countByStatus("rejected");
        long paidCount = rebateRepository.countByStatus("paid");
        
        statistics.put("pendingCount", pendingCount);
        statistics.put("approvedCount", approvedCount);
        statistics.put("rejectedCount", rejectedCount);
        statistics.put("paidCount", paidCount);
        statistics.put("totalCount", pendingCount + approvedCount + rejectedCount + paidCount);
        
        // 统计返点金额
        Double totalPendingAmount = rebateRepository.sumRebateAmountByStatus("pending");
        Double totalPaidAmount = rebateRepository.sumRebateAmountByStatus("paid");
        
        statistics.put("totalPendingAmount", totalPendingAmount != null ? totalPendingAmount : 0.0);
        statistics.put("totalPaidAmount", totalPaidAmount != null ? totalPaidAmount : 0.0);
        
        return statistics;
    }
}
