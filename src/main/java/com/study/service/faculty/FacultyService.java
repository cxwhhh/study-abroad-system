package com.study.service.faculty;

import java.util.List;

/**
 * 学院服务接口
 */
public interface FacultyService {
    
    /**
     * 获取所有学院
     * @return 学院列表
     */
    List<Faculty> getAllFaculties();
    
    /**
     * 根据ID获取学院
     * @param id 学院ID
     * @return 学院信息
     */
    Faculty getFacultyById(Long id);
    
    /**
     * 根据院校ID获取所有学院
     * @param schoolId 院校ID
     * @return 学院列表
     */
    List<Faculty> getFacultiesBySchoolId(Long schoolId);
    
    /**
     * 添加学院
     * @param faculty 学院信息
     * @return 添加后的学院信息（包含ID）
     */
    Faculty addFaculty(Faculty faculty);
    
    /**
     * 更新学院信息
     * @param faculty 学院信息
     * @return 更新结果
     */
    boolean updateFaculty(Faculty faculty);
    
    /**
     * 删除学院
     * @param id 学院ID
     * @return 删除结果
     */
    boolean deleteFaculty(Long id);
} 
