package com.study.controller;

import com.study.service.matching.MatchingResult;
import com.study.service.matching.MatchingResultService;
import com.study.service.matching.dto.MatchingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 匹配算法控制器
 * 提供学校匹配相关的 REST API
 */
@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private static final Logger log = LoggerFactory.getLogger(MatchingController.class);

    @Autowired
    private MatchingResultService matchingResultService;




    /**
     * 2. 【核心接口】单独检测某所学校
     * 前端传入学生背景 + 学校名称，只跑该学校的算法
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSchool(
            @RequestBody MatchingRequest request,
            @RequestParam String schoolName) {
        if (schoolName != null) {
            schoolName = schoolName.trim();
        }
        if (schoolName == null || schoolName.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "schoolName 不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        // 将单一学校名称包装进请求中
        request.setTargetSchools(Arrays.asList(schoolName));
        return executeMatching(request);
    }

    /**
     * 1. 【核心接口】智能一键选校
     * 前端传入学生背景，后端自动跑遍所有支持的学校算法
     */
    @PostMapping("/match")
    // 修改点1: private 改为 public
    // 修改点2: 添加 @RequestBody 注解
    public ResponseEntity<Map<String, Object>> executeMatching(@RequestBody MatchingRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = request.getStudentInfo() == null ? null : request.getStudentInfo().getUserId();
            log.info("匹配请求: region={}, userId={}, targetSchools={}",
                    request.getRegion(), userId, request.getTargetSchools());

            // 调用 Service 执行全流程（计算 + 入库）
            List<MatchingResult> results = matchingResultService.performMatching(request);

            response.put("success", true);
            response.put("message", "匹配完成");
            response.put("data", results);
            response.put("total", results.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 这里捕获了 Service 层抛出的参数校验异常
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "参数错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系统内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // =================================================================================
    // 以下查询和管理接口保持不变 (Result Query & Management APIs)
    // =================================================================================

    /**
     * 获取用户的匹配结果（分页）
     */
    @GetMapping("/results/{userId}")
    public ResponseEntity<Map<String, Object>> getMatchingResults(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<MatchingResult> results = matchingResultService.getMatchingResultsByUserId(userId, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("total", results.getTotalElements());
            response.put("totalPages", results.getTotalPages());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取匹配结果失败: " + e.getMessage());
        }
    }

    /**
     * 根据地区获取用户的匹配结果
     */
    @GetMapping("/results/{userId}/region/{region}")
    public ResponseEntity<Map<String, Object>> getMatchingResultsByRegion(
            @PathVariable Long userId, @PathVariable String region) {
        try {
            List<MatchingResult> results = matchingResultService.getMatchingResultsByUserIdAndRegion(userId, region);
            return buildSuccessResponse(results);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 获取用户收藏的匹配结果
     */
    @GetMapping("/results/{userId}/favorites")
    public ResponseEntity<Map<String, Object>> getFavoriteResults(@PathVariable Long userId) {
        try {
            List<MatchingResult> results = matchingResultService.getFavoriteMatchingResults(userId);
            return buildSuccessResponse(results);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 收藏/取消收藏匹配结果
     */
    @PutMapping("/results/{resultId}/favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long resultId,
            @RequestParam Long userId,
            @RequestParam boolean isFavorite) {
        try {
            MatchingResult result = matchingResultService.toggleFavorite(resultId, userId, isFavorite);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isFavorite ? "收藏成功" : "取消收藏成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 删除匹配结果
     */
    @DeleteMapping("/results/{resultId}")
    public ResponseEntity<Map<String, Object>> deleteResult(
            @PathVariable Long resultId, @RequestParam Long userId) {
        try {
            matchingResultService.deleteMatchingResult(resultId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    /**
     * 辅助方法：构建成功响应
     */
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        if (data instanceof List) {
            response.put("total", ((List<?>) data).size());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 辅助方法：构建失败响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
