package com.study.service.schools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.service.application.Application;
import com.study.service.application.ApplicationService;
import com.study.service.faculty.Faculty;
import com.study.service.faculty.FacultyService;
import com.study.service.major.Major;
import com.study.service.major.MajorService;

/**
 * 院校控制器
 */
@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private static final Logger logger = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    /**
     * 获取所有院校
     * 
     * @return 院校列表
     */
    @GetMapping
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(schools);
    }

    /**
     * 根据ID获取院校
     * 
     * @param id 院校ID
     * @return 院校信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable Long id) {
        School school = schoolService.getSchoolById(id);
        if (school != null) {
            return ResponseEntity.ok(school);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据国家筛选院校
     * 
     * @param country 国家
     * @return 院校列表
     */
    @GetMapping("/filter/country/{country}")
    public ResponseEntity<List<School>> getSchoolsByCountry(@PathVariable String country) {
        List<School> schools = schoolService.getSchoolsByCountry(country);
        return ResponseEntity.ok(schools);
    }

    /**
     * 根据类型筛选院校
     * 
     * @param type 院校类型
     * @return 院校列表
     */
    @GetMapping("/filter/type/{type}")
    public ResponseEntity<List<School>> getSchoolsByType(@PathVariable String type) {
        List<School> schools = schoolService.getSchoolsByType(type);
        return ResponseEntity.ok(schools);
    }

    /**
     * 搜索院校
     * 
     * @param keyword 关键词
     * @return 院校列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<School>> searchSchools(@RequestParam String keyword) {
        List<School> schools = schoolService.searchSchools(keyword);
        return ResponseEntity.ok(schools);
    }

    /**
     * 添加院校
     * 
     * @param school 院校信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<School> addSchool(@RequestBody School school) {
        School savedSchool = schoolService.addSchool(school);
        return new ResponseEntity<>(savedSchool, HttpStatus.CREATED);
    }

    /**
     * 更新院校信息
     * 
     * @param id     院校ID
     * @param school 院校信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSchool(
            @PathVariable Long id,
            @RequestBody School school) {

        // 确保ID一致
        school.setId(id);

        boolean updated = schoolService.updateSchool(school);
        Map<String, Object> response = new HashMap<>();

        if (updated) {
            response.put("success", true);
            response.put("message", "院校信息更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "院校信息更新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除院校
     * 
     * @param id 院校ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSchool(@PathVariable Long id) {
        boolean deleted = schoolService.deleteSchool(id);
        Map<String, Object> response = new HashMap<>();

        if (deleted) {
            response.put("success", true);
            response.put("message", "院校删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "院校删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取院校的申请数据
     * 
     * @param id 院校ID
     * @return 申请数据列表
     */
    @GetMapping("/{id}/applications")
    public ResponseEntity<List<Application>> getSchoolApplications(@PathVariable Long id) {
        List<Application> applications = applicationService.getApplicationsBySchoolId(id);
        return ResponseEntity.ok(applications);
    }

    /**
     * 获取院校的学院列表
     * 
     * @param id 院校ID
     * @return 学院列表
     */
    @GetMapping("/{id}/faculties")
    public ResponseEntity<?> getSchoolFaculties(@PathVariable Long id) {
        try {
            logger.info("开始获取院校 {} 的学院列表", id);
            List<Faculty> faculties = facultyService.getFacultiesBySchoolId(id);
            logger.info("成功获取到院校 {} 的 {} 个学院", id, faculties.size());
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            logger.error("获取院校 {} 的学院列表失败", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取学院列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/bulk-details")
    public ResponseEntity<?> getBulkSchoolDetails(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> schoolIds = request != null ? request.get("schoolIds") : null;
            if (schoolIds == null || schoolIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "schoolIds 不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Long schoolId : schoolIds) {
                List<Faculty> faculties = facultyService.getFacultiesBySchoolId(schoolId);
                int totalMajorCount = 0;
                for (Faculty faculty : faculties) {
                    if (faculty != null && faculty.getMajors() != null) {
                        totalMajorCount += faculty.getMajors().size();
                    }
                }

                Map<String, Object> item = new HashMap<>();
                item.put("schoolId", schoolId);
                item.put("faculties", faculties);
                item.put("totalMajorCount", totalMajorCount);
                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("批量获取学校详细信息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量获取学校详细信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/bulk-details")
    public ResponseEntity<?> getBulkSchoolDetailsGet(
            @RequestParam(name = "schoolIds", required = false) String schoolIdsParam) {
        try {
            if (schoolIdsParam == null || schoolIdsParam.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "schoolIds 不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            String[] parts = schoolIdsParam.split(",");
            List<Long> schoolIds = new ArrayList<>();
            for (String part : parts) {
                String trimmed = part == null ? "" : part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                schoolIds.add(Long.parseLong(trimmed));
            }

            if (schoolIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "schoolIds 不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Long schoolId : schoolIds) {
                List<Faculty> faculties = facultyService.getFacultiesBySchoolId(schoolId);
                int totalMajorCount = 0;
                for (Faculty faculty : faculties) {
                    if (faculty != null && faculty.getMajors() != null) {
                        totalMajorCount += faculty.getMajors().size();
                    }
                }

                Map<String, Object> item = new HashMap<>();
                item.put("schoolId", schoolId);
                item.put("faculties", faculties);
                item.put("totalMajorCount", totalMajorCount);
                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("批量获取学校详细信息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量获取学校详细信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定学校下的所有专业
     * 
     * @param schoolId 学校ID
     * @return 专业列表
     */
    @GetMapping("/{schoolId}/majors")
    public ResponseEntity<List<Major>> getMajorsBySchoolId(@PathVariable Long schoolId) {
        List<Major> majors = majorService.getMajorsBySchoolId(schoolId);
        return ResponseEntity.ok(majors);
    }

    /**
     * 获取指定学校的指定学院下的所有专业
     * 
     * @param schoolId  学校ID
     * @param facultyId 学院ID
     * @return 专业列表
     */
    @GetMapping("/{schoolId}/faculties/{facultyId}/majors")
    public ResponseEntity<List<Major>> getMajorsBySchoolAndFaculty(
            @PathVariable Long schoolId,
            @PathVariable Long facultyId) {

        // 获取该学院下的所有专业
        List<Major> majors = majorService.getMajorsByFacultyId(facultyId);

        // 筛选出属于指定学校的专业（虽然通常情况下，如果学院ID正确，所有专业都应该属于这个学校）
        majors.removeIf(major -> !major.getSchoolId().equals(schoolId));

        return ResponseEntity.ok(majors);
    }
}
