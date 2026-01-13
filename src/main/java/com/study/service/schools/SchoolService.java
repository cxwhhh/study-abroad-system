package com.study.service.schools;

import java.util.List;

/**
 * 院校服务接口
 */
public interface SchoolService {
    
    /**
     * 获取所有院校
     * @return 院校列表
     */
    List<School> getAllSchools();
    
    /**
     * 根据ID获取院校
     * @param id 院校ID
     * @return 院校信息
     */
    School getSchoolById(Long id);
    
    /**
     * 根据国家筛选院校
     * @param country 国家
     * @return 院校列表
     */
    List<School> getSchoolsByCountry(String country);
    
    /**
     * 根据类型筛选院校
     * @param type 院校类型
     * @return 院校列表
     */
    List<School> getSchoolsByType(String type);
    
    /**
     * 添加院校
     * @param school 院校信息
     * @return 添加后的院校信息（包含ID）
     */
    School addSchool(School school);
    
    /**
     * 更新院校信息
     * @param school 院校信息
     * @return 更新结果
     */
    boolean updateSchool(School school);
    
    /**
     * 删除院校
     * @param id 院校ID
     * @return 删除结果
     */
    boolean deleteSchool(Long id);
    
    /**
     * 搜索院校
     * @param keyword 关键词
     * @return 院校列表
     */
    List<School> searchSchools(String keyword);
} 