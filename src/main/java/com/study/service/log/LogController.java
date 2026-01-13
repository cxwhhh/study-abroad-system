package com.study.service.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志信息控制器
 */
@RestController
@RequestMapping("/api/log")
@Slf4j
public class LogController {
    
    @Autowired
    private LogService logService;
    
    /**
     * 获取日志列表
     */
    @GetMapping("/list")
    @LogOperation(module = "系统日志", description = "查询日志列表")
    public ResponseEntity<Map<String, Object>> getLogsList(LogQueryParams params) {
        log.info("查询日志列表，参数: {}", params);
        Map<String, Object> result = logService.getLogsList(params);
        return createSuccessResponse(result);
    }
    
    /**
     * 获取学生相关日志
     */
    @GetMapping("/student")
    @LogOperation(module = "系统日志", description = "查询学生相关日志")
    public ResponseEntity<Map<String, Object>> getStudentLogs(LogQueryParams params) {
        log.info("查询学生相关日志，参数: {}", params);
        Map<String, Object> result = logService.getStudentLogs(params);
        return createSuccessResponse(result);
    }
    
    /**
     * 获取主页操作日志
     */
    @GetMapping("/homepage")
    @LogOperation(module = "系统日志", description = "查询主页操作日志")
    public ResponseEntity<Map<String, Object>> getHomepageLogs(LogQueryParams params) {
        log.info("查询主页操作日志，参数: {}", params);
        Map<String, Object> result = logService.getHomepageLogs(params);
        return createSuccessResponse(result);
    }
    
    /**
     * 获取日志详情
     */
    @GetMapping("/detail/{id}")
    @LogOperation(module = "系统日志", description = "查询日志详情", saveResult = true)
    public ResponseEntity<Map<String, Object>> getLogDetail(@PathVariable Long id) {
        log.info("查询日志详情，ID: {}", id);
        Log logDetail = logService.getLogDetail(id);
        if (logDetail != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "操作成功");
            result.put("data", logDetail);
            return ResponseEntity.ok(result);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("msg", "未找到相关日志");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
    
    /**
     * 获取日志类型选项
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getLogTypes() {
        log.info("获取日志类型选项");
        List<Map<String, String>> types = logService.getLogTypes();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "操作成功");
        result.put("data", types);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取系统模块选项
     */
    @GetMapping("/modules")
    public ResponseEntity<Map<String, Object>> getModules() {
        log.info("获取系统模块选项");
        List<Map<String, String>> modules = logService.getModules();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "操作成功");
        result.put("data", modules);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 导出日志
     */
    @GetMapping("/export")
    @LogOperation(module = "系统日志", description = "导出日志")
    public ResponseEntity<byte[]> exportLogs(LogQueryParams params, HttpServletResponse response) {
        log.info("导出日志，参数: {}", params);
        
        try {
            byte[] data = logService.exportLogs(params);
            if (data.length > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateTime = sdf.format(new Date());
                String fileName = "system_log_" + dateTime + ".xlsx";
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);
                
                return new ResponseEntity<>(data, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            log.error("导出日志失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 清理过期日志
     */
    @GetMapping("/clean/{days}")
    @LogOperation(module = "系统日志", description = "清理过期日志", operType = "3")
    public ResponseEntity<Map<String, Object>> cleanLogs(@PathVariable Integer days) {
        log.info("清理过期日志，保留天数: {}", days);
        boolean result = logService.cleanLogs(days);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("msg", "清理成功");
        } else {
            response.put("code", 500);
            response.put("msg", "清理失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建成功响应
     */
    private ResponseEntity<Map<String, Object>> createSuccessResponse(Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "操作成功");
        response.putAll(data);
        return ResponseEntity.ok(response);
    }
} 