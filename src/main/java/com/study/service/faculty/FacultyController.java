package com.study.service.faculty;

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

import com.study.service.major.Major;
import com.study.service.major.MajorService;

/**
 * 学院控制器
 */
@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;
    
    @Autowired
    private MajorService majorService;
    
    /**
     * 获取所有学院
     * @return 学院列表
     */
    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        List<Faculty> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }
    
    /**
     * 根据ID获取学院
     * @param id 学院ID
     * @return 学院信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        Faculty faculty = facultyService.getFacultyById(id);
        if (faculty != null) {
            return ResponseEntity.ok(faculty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 添加学院
     * @param faculty 学院信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        Faculty savedFaculty = facultyService.addFaculty(faculty);
        return new ResponseEntity<>(savedFaculty, HttpStatus.CREATED);
    }
    
    /**
     * 更新学院信息
     * @param id 学院ID
     * @param faculty 学院信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFaculty(
            @PathVariable Long id, 
            @RequestBody Faculty faculty) {
        
        // 确保ID一致
        faculty.setId(id);
        
        boolean updated = facultyService.updateFaculty(faculty);
        Map<String, Object> response = new HashMap<>();
        
        if (updated) {
            response.put("success", true);
            response.put("message", "学院信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "学院信息更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除学院
     * @param id 学院ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFaculty(@PathVariable Long id) {
        boolean deleted = facultyService.deleteFaculty(id);
        Map<String, Object> response = new HashMap<>();
        
        if (deleted) {
            response.put("success", true);
            response.put("message", "学院删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "学院删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取学院的专业列表
     * @param id 学院ID
     * @return 专业列表
     */
    @GetMapping("/{id}/majors")
    public ResponseEntity<List<Major>> getFacultyMajors(@PathVariable Long id) {
        List<Major> majors = majorService.getMajorsByFacultyId(id);
        return ResponseEntity.ok(majors);
    }
}