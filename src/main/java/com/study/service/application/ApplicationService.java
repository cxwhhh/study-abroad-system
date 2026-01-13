package com.study.service.application;

import java.util.List;

/**
 * 申请服务接口
 */
public interface ApplicationService {
    
    /**
     * 获取所有申请
     * @return 申请列表
     */
    List<Application> getAllApplications();
    
    /**
     * 根据ID获取申请
     * @param id 申请ID
     * @return 申请信息
     */
    Application getApplicationById(Long id);
    
    /**
     * 根据用户ID获取所有申请
     * @param userId 用户ID
     * @return 申请列表
     */
    List<Application> getApplicationsByUserId(Long userId);
    
    /**
     * 根据院校ID获取所有申请
     * @param schoolId 院校ID
     * @return 申请列表
     */
    List<Application> getApplicationsBySchoolId(Long schoolId);
    
    /**
     * 根据专业ID获取所有申请
     * @param majorId 专业ID
     * @return 申请列表
     */
    List<Application> getApplicationsByMajorId(Long majorId);
    
    /**
     * 根据申请状态获取所有申请
     * @param status 申请状态
     * @return 申请列表
     */
    List<Application> getApplicationsByStatus(String status);
    
    /**
     * 添加申请
     * @param application 申请信息
     * @return 添加后的申请信息（包含ID）
     */
    Application addApplication(Application application);
    
    /**
     * 更新申请信息
     * @param application 申请信息
     * @return 更新结果
     */
    boolean updateApplication(Application application);
    
    /**
     * 删除申请
     * @param id 申请ID
     * @return 删除结果
     */
    boolean deleteApplication(Long id);
} 