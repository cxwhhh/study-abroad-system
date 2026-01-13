package com.study.service.enrollment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.service.applicationschool.ApplicationSchool;
import com.study.service.applicationschool.ApplicationSchoolService;

/**
 * 入学信息管理控制器
 */
@RestController
@RequestMapping("/api/admin/applications")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ApplicationSchoolService applicationSchoolService;

    /**
     * 完成申请 - 创建入学记录
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeApplication(@RequestBody CompleteApplicationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证申请是否存在
            ApplicationSchool application = applicationSchoolService.getApplicationDetail(request.getApplicationId());
            if (application == null) {
                response.put("success", false);
                response.put("message", "申请不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查申请状态是否为"已入学"
            if (!"enrolled".equals(application.getApplicationStatus())) {
                response.put("success", false);
                response.put("message", "只有已入学状态的申请才能完成申请");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查是否已经有入学记录
            if (enrollmentService.existsByApplicationId(request.getApplicationId())) {
                response.put("success", false);
                response.put("message", "该申请已经有入学记录");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建入学记录
            Enrollment enrollment = enrollmentService.createEnrollment(
                request.getApplicationId(),
                application.getUserId(),
                application.getSchoolId(),
                request.getProgramId(),
                request.getEnrollmentDate() != null ? request.getEnrollmentDate() : new Date(),
                request.getCreatedBy()
            );
            
            // 如果提供了学费和返点金额，更新记录
            if (request.getTuitionFee() != null) {
                enrollment.setTuitionFee(request.getTuitionFee());
            }
            if (request.getCommissionAmount() != null) {
                enrollment.setCommissionAmount(request.getCommissionAmount());
            }
            if (request.getNotes() != null) {
                enrollment.setNotes(request.getNotes());
            }
            
            response.put("success", true);
            response.put("message", "入学记录创建成功");
            response.put("data", enrollment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建入学记录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取入学信息列表
     */
    @GetMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> getEnrollments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String commissionStatus,
            @RequestParam(required = false) Integer academicYear,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Enrollment> enrollments = enrollmentService.getEnrollments(
                studentId, schoolId, commissionStatus, academicYear, startDate, endDate, pageable);
            
            response.put("success", true);
            response.put("data", enrollments.getContent());
            response.put("totalElements", enrollments.getTotalElements());
            response.put("totalPages", enrollments.getTotalPages());
            response.put("currentPage", enrollments.getNumber());
            response.put("size", enrollments.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取入学信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新返点状态
     */
    @PutMapping("/{id}/commission-status")
    public ResponseEntity<Map<String, Object>> updateCommissionStatus(
            @PathVariable Long id,
            @RequestBody UpdateCommissionStatusRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Enrollment enrollment = enrollmentService.updateCommissionStatus(
                id, request.getCommissionStatus(), request.getCommissionAmount());
            
            response.put("success", true);
            response.put("message", "返点状态更新成功");
            response.put("data", enrollment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新返点状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取返点统计信息
     */
    @GetMapping("/enrollments/commission-statistics")
    public ResponseEntity<Map<String, Object>> getCommissionStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics = enrollmentService.getCommissionStatistics();
            
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取返点统计失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取待支付返点列表
     */
    @GetMapping("/enrollments/pending-commissions")
    public ResponseEntity<Map<String, Object>> getPendingCommissions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Enrollment> pendingCommissions = enrollmentService.getPendingCommissions();
            
            response.put("success", true);
            response.put("data", pendingCommissions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取待支付返点失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 完成申请请求DTO
     */
    public static class CompleteApplicationRequest {
        private Long applicationId;
        private Long programId;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Date enrollmentDate;
        
        private Double tuitionFee;
        private Double commissionAmount;
        private String notes;
        private Long createdBy;

        // Getters and Setters
        public Long getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(Long applicationId) {
            this.applicationId = applicationId;
        }

        public Long getProgramId() {
            return programId;
        }

        public void setProgramId(Long programId) {
            this.programId = programId;
        }

        public Date getEnrollmentDate() {
            return enrollmentDate;
        }

        public void setEnrollmentDate(Date enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
        }

        public Double getTuitionFee() {
            return tuitionFee;
        }

        public void setTuitionFee(Double tuitionFee) {
            this.tuitionFee = tuitionFee;
        }

        public Double getCommissionAmount() {
            return commissionAmount;
        }

        public void setCommissionAmount(Double commissionAmount) {
            this.commissionAmount = commissionAmount;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }
    }

    /**
     * 更新返点状态请求DTO
     */
    public static class UpdateCommissionStatusRequest {
        private String commissionStatus;
        private Double commissionAmount;

        // Getters and Setters
        public String getCommissionStatus() {
            return commissionStatus;
        }

        public void setCommissionStatus(String commissionStatus) {
            this.commissionStatus = commissionStatus;
        }

        public Double getCommissionAmount() {
            return commissionAmount;
        }

        public void setCommissionAmount(Double commissionAmount) {
            this.commissionAmount = commissionAmount;
        }
    }
}
