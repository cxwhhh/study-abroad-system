package com.study.service.major;

import java.util.List;

/**
 * 专业服务接口
 */
public interface MajorService {
    
    /**
     * 获取所有专业
     * @return 专业列表
     */
    List<Major> getAllMajors();
    
    /**
     * 根据ID获取专业
     * @param id 专业ID
     * @return 专业信息
     */
    Major getMajorById(Long id);
    
    /**
     * 根据学院ID获取所有专业
     * @param facultyId 学院ID
     * @return 专业列表
     */
    List<Major> getMajorsByFacultyId(Long facultyId);
    
    /**
     * 根据院校ID获取所有专业
     * @param schoolId 院校ID
     * @return 专业列表
     */
    List<Major> getMajorsBySchoolId(Long schoolId);
    
    /**
     * 添加专业
     * @param major 专业信息
     * @return 添加后的专业信息（包含ID）
     */
    Major addMajor(Major major);
    
    /**
     * 更新专业信息
     * @param major 专业信息
     * @return 更新结果
     */
    boolean updateMajor(Major major);
    
    /**
     * 删除专业
     * @param id 专业ID
     * @return 删除结果
     */
    boolean deleteMajor(Long id);
} 