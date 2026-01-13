package com.study.service.major;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 专业控制器
 */
@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;
    
    @Autowired
    private CoreCourseService coreCourseService;
    
    /**
     * 获取所有专业
     * @return 专业列表
     */
    @GetMapping
    public ResponseEntity<List<Major>> getAllMajors() {
        List<Major> majors = majorService.getAllMajors();
        return ResponseEntity.ok(majors);
    }
    
    /**
     * 根据ID获取专业
     * @param id 专业ID
     * @return 专业信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Major> getMajorById(@PathVariable Long id) {
        Major major = majorService.getMajorById(id);
        if (major != null) {
            return ResponseEntity.ok(major);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 添加专业
     * @param major 专业信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<Major> addMajor(@RequestBody Major major) {
        Major savedMajor = majorService.addMajor(major);
        return new ResponseEntity<>(savedMajor, HttpStatus.CREATED);
    }
    
    /**
     * 更新专业信息
     * @param id 专业ID
     * @param major 专业信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMajor(
            @PathVariable Long id, 
            @RequestBody Major major) {
        
        // 确保ID一致
        major.setId(id);
        
        boolean updated = majorService.updateMajor(major);
        Map<String, Object> response = new HashMap<>();
        
        if (updated) {
            response.put("success", true);
            response.put("message", "专业信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "专业信息更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除专业
     * @param id 专业ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMajor(@PathVariable Long id) {
        boolean deleted = majorService.deleteMajor(id);
        Map<String, Object> response = new HashMap<>();
        
        if (deleted) {
            response.put("success", true);
            response.put("message", "专业删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "专业删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取专业的核心课程列表
     * @param majorId 专业ID
     * @return 核心课程列表
     */
    @GetMapping("/{majorId}/core-courses")
    public ResponseEntity<List<CoreCourse>> getCoreCoursesByMajorId(@PathVariable Long majorId) {
        List<CoreCourse> coreCourses = coreCourseService.getCoreCoursesByMajorId(majorId);
        return ResponseEntity.ok(coreCourses);
    }
    
    /**
     * 根据课程类型获取专业的核心课程
     * @param majorId 专业ID
     * @param courseType 课程类型（必修/选修）
     * @return 核心课程列表
     */
    @GetMapping("/{majorId}/core-courses/type/{courseType}")
    public ResponseEntity<List<CoreCourse>> getCoreCoursesByMajorIdAndType(
            @PathVariable Long majorId, 
            @PathVariable String courseType) {
        List<CoreCourse> coreCourses = coreCourseService.getCoreCoursesByMajorIdAndType(majorId, courseType);
        return ResponseEntity.ok(coreCourses);
    }
    
    /**
     * 根据学期获取专业的核心课程
     * @param majorId 专业ID
     * @param semester 学期
     * @return 核心课程列表
     */
    @GetMapping("/{majorId}/core-courses/semester/{semester}")
    public ResponseEntity<List<CoreCourse>> getCoreCoursesByMajorIdAndSemester(
            @PathVariable Long majorId, 
            @PathVariable Integer semester) {
        List<CoreCourse> coreCourses = coreCourseService.getCoreCoursesByMajorIdAndSemester(majorId, semester);
        return ResponseEntity.ok(coreCourses);
    }
    
    /**
     * 统计专业的核心课程数量
     * @param majorId 专业ID
     * @return 课程数量
     */
    @GetMapping("/{majorId}/core-courses/count")
    public ResponseEntity<Map<String, Object>> getCoreCourseCountByMajorId(@PathVariable Long majorId) {
        Long count = coreCourseService.countCoreCoursesByMajorId(majorId);
        Map<String, Object> response = new HashMap<>();
        response.put("majorId", majorId);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}