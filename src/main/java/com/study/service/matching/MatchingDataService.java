package com.study.service.matching;

import com.study.service.schools.School;
import com.study.service.major.Major;
import com.study.service.faculty.Faculty;
import com.study.service.students.Student;
import com.study.service.schools.SchoolService;
import com.study.service.major.MajorService;
import com.study.service.faculty.FacultyService;
import com.study.service.students.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 匹配算法数据服务
 * 为匹配算法提供数据库访问功能
 */
@Service
public class MatchingDataService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    /**
     * 根据地区获取学校列表
     * @param region 地区名称（如：英国、美国、澳大利亚等）
     * @return 该地区的学校列表
     */
    public List<School> getSchoolsByRegion(String region) {
        List<School> allSchools = schoolService.getAllSchools();
        return allSchools.stream()
                .filter(school -> isSchoolInRegion(school, region))
                .collect(Collectors.toList());
    }

    /**
     * 根据学校ID获取学校信息
     * @param schoolId 学校ID
     * @return 学校信息
     */
    public School getSchoolById(Long schoolId) {
        return schoolService.getSchoolById(schoolId);
    }

    /**
     * 根据学校名称获取学校信息
     * @param schoolName 学校名称
     * @return 学校信息
     */
    public School getSchoolByName(String schoolName) {
        List<School> schools = schoolService.searchSchools(schoolName);
        return schools.stream()
                .filter(school -> school.getName().equals(schoolName) || 
                               (school.getEnglishName() != null && school.getEnglishName().equals(schoolName)))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取学校的所有专业
     * @param schoolId 学校ID
     * @return 专业列表
     */
    public List<Major> getMajorsBySchoolId(Long schoolId) {
        return majorService.getMajorsBySchoolId(schoolId);
    }

    /**
     * 根据专业名称和学校ID查找专业
     * @param majorName 专业名称
     * @param schoolId 学校ID
     * @return 专业信息
     */
    public Major getMajorByNameAndSchool(String majorName, Long schoolId) {
        List<Major> majors = getMajorsBySchoolId(schoolId);
        return majors.stream()
                .filter(major -> major.getName().contains(majorName) || 
                               (major.getEnglishName() != null && major.getEnglishName().contains(majorName)))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取学校的所有学院
     * @param schoolId 学校ID
     * @return 学院列表
     */
    public List<Faculty> getFacultiesBySchoolId(Long schoolId) {
        return facultyService.getFacultiesBySchoolId(schoolId);
    }

    /**
     * 根据用户ID获取学生信息
     * @param userId 用户ID
     * @return 学生信息
     */
    public Student getStudentByUserId(Long userId) {
        return studentService.getStudentByUserId(userId);
    }

    /**
     * 判断学校是否在指定地区
     * @param school 学校信息
     * @param region 地区名称
     * @return 是否在该地区
     */
    private boolean isSchoolInRegion(School school, String region) {
        if (school.getCountry() == null) {
            return false;
        }
        
        String country = school.getCountry().toLowerCase();
        String regionLower = region.toLowerCase();
        
        // 地区映射
        switch (regionLower) {
            case "英国":
            case "uk":
            case "united kingdom":
                return country.contains("英国") || country.contains("uk") || 
                       country.contains("united kingdom") || country.contains("britain");
                       
            case "美国":
            case "usa":
            case "us":
            case "united states":
                return country.contains("美国") || country.contains("usa") || 
                       country.contains("us") || country.contains("united states") ||
                       country.contains("america");
                       
            case "澳大利亚":
            case "澳洲":
            case "australia":
                return country.contains("澳大利亚") || country.contains("澳洲") || 
                       country.contains("australia");
                       
            case "新加坡":
            case "singapore":
                return country.contains("新加坡") || country.contains("singapore");
                
            case "香港":
            case "hong kong":
            case "hk":
                return country.contains("香港") || country.contains("hong kong") || 
                       country.contains("hk");
                       
            default:
                return country.contains(regionLower);
        }
    }

    /**
     * 根据多个条件搜索学校
     * @param region 地区
     * @param schoolType 学校类型
     * @param degreeLevel 学位级别
     * @return 符合条件的学校列表
     */
    public List<School> searchSchools(String region, String schoolType, String degreeLevel) {
        List<School> schools = getSchoolsByRegion(region);
        
        return schools.stream()
                .filter(school -> {
                    // 按学校类型过滤
                    if (schoolType != null && !schoolType.isEmpty()) {
                        if (school.getType() == null || !school.getType().contains(schoolType)) {
                            return false;
                        }
                    }
                    
                    // 按学位级别过滤
                    if (degreeLevel != null && !degreeLevel.isEmpty()) {
                        switch (degreeLevel.toLowerCase()) {
                            case "本科":
                            case "undergraduate":
                                return school.getHasUndergraduate() != null && school.getHasUndergraduate();
                            case "硕士":
                            case "研究生":
                            case "graduate":
                            case "master":
                                return school.getHasGraduate() != null && school.getHasGraduate();
                            case "博士":
                            case "phd":
                            case "doctorate":
                                return school.getHasPhd() != null && school.getHasPhd();
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取所有可用的地区列表
     * @return 地区列表
     */
    public List<String> getAvailableRegions() {
        List<School> allSchools = schoolService.getAllSchools();
        return allSchools.stream()
                .map(School::getCountry)
                .filter(country -> country != null && !country.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}