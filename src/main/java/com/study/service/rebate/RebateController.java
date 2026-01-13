package com.study.service.rebate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.study.service.files.FileService;

/**
 * 返点申请控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Slf4j
public class RebateController {

    @Autowired
    private RebateService rebateService;

    @Autowired
    private FileService fileService;

    /**
     * 验证学生申请信息（申请ID、学校、专业）
     */
    @PostMapping("/rebate/validate-application")
    public ResponseEntity<Map<String, Object>> validateApplication(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            
            // 如果请求中包含学校和专业信息，则进行完整验证
            if (request.containsKey("schoolName") && request.containsKey("majorName")) {
                String schoolName = request.get("schoolName").toString();
                String majorName = request.get("majorName").toString();
                
                // 先验证申请ID
                Map<String, Object> idValidationResult = rebateService.validateStudentApplication(applicationId);
                if (!(Boolean) idValidationResult.get("success")) {
                    return ResponseEntity.ok(idValidationResult);
                }
                
                // 再验证学校和专业信息
                Map<String, Object> result = rebateService.validateSchoolAndMajor(applicationId, schoolName, majorName);
                return ResponseEntity.ok(result);
            } else {
                // 只验证申请ID
                Map<String, Object> result = rebateService.validateStudentApplication(applicationId);
                return ResponseEntity.ok(result);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "验证申请信息时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 验证学校和专业信息
     */
    @PostMapping("/rebate/validate-school-major")
    public ResponseEntity<Map<String, Object>> validateSchoolMajor(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            String schoolName = request.get("schoolName").toString();
            String majorName = request.get("majorName").toString();
            
            Map<String, Object> result = rebateService.validateSchoolAndMajor(applicationId, schoolName, majorName);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "验证学校专业信息时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 提交返点申请
     */
    @PostMapping("/rebate/submit-application")
    public ResponseEntity<Map<String, Object>> submitApplication(@RequestBody RebateApplicationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> result = rebateService.submitRebateApplication(request);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "提交返点申请时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取返点申请详情
     */
    @GetMapping("/rebate/{id}")
    public ResponseEntity<Map<String, Object>> getRebateDetail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Rebate> rebateOpt = rebateService.getRebateById(id);
            
            if (rebateOpt.isPresent()) {
                response.put("success", true);
                response.put("data", rebateOpt.get());
            } else {
                response.put("success", false);
                response.put("message", "未找到指定的返点申请");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取返点申请详情时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据入学记录ID获取返点申请列表
     */
    @GetMapping("/rebate/enrollment/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> getRebatesByEnrollment(@PathVariable Long enrollmentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Rebate> rebates = rebateService.getRebatesByEnrollmentId(enrollmentId);
            
            response.put("success", true);
            response.put("data", rebates);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取返点申请列表时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取返点申请统计信息
     */
    @GetMapping("/rebate/statistics")
    public ResponseEntity<Map<String, Object>> getRebateStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics = rebateService.getRebateStatistics();
            
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取返点统计信息时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算预估返点金额
     */
    @PostMapping("/rebate/calculate-amount")
    public ResponseEntity<Map<String, Object>> calculateRebateAmount(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            
            // 先验证申请ID
            Map<String, Object> validationResult = rebateService.validateStudentApplication(applicationId);
            
            if (!(Boolean) validationResult.get("success")) {
                return ResponseEntity.ok(validationResult);
            }
            
            // 计算返点金额
            // 这里可以根据学费、学校等因素计算预估返点金额
            response.put("success", true);
            response.put("message", "返点金额计算成功");
            response.put("estimatedAmount", 5000.0); // 示例金额
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "计算返点金额时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 上传返点申请文件
     */
    @PostMapping("/rebate/upload-file")
    public ResponseEntity<Map<String, Object>> uploadRebateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestParam("applicationId") Long applicationId) {
        
        log.info("接收到返点申请文件上传请求: fileName={}, size={}, fileType={}, applicationId={}",
                file != null ? file.getOriginalFilename() : "null", 
                file != null ? file.getSize() : "null", 
                fileType, applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        // 检查文件是否存在
        if (file == null || file.isEmpty()) {
            log.error("未检测到文件或文件为空");
            response.put("success", false);
            response.put("message", "未检测到文件，请选择要上传的文件");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            // 映射文件类型名称
            String materialType;
            switch (fileType) {
                case "admissionLetter":
                    materialType = "录取验证";
                    break;
                case "enrollmentProof":
                    materialType = "入学验证";
                    break;
                default:
                    materialType = fileType;
                    break;
            }
            
            // 使用FileService存储返点申请验证文件
            Map<String, Object> fileResult = fileService.storeRebateVerifyFile(file, materialType, applicationId);
            String filePath = (String) fileResult.get("filePath");
            Long fileId = (Long) fileResult.get("fileId");
            
            log.info("返点申请文件上传成功: {}, 文件ID: {}, 类型: {}, 申请ID: {}", filePath, fileId, materialType, applicationId);
            
            response.put("success", true);
            response.put("filePath", filePath);
            response.put("fileId", fileId);
            response.put("materialType", materialType);
            response.put("fileType", fileType);
            response.put("applicationId", applicationId);
            response.put("message", "文件上传成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("返点申请文件上传失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("error", e.getClass().getName());
            response.put("errorDetail", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}