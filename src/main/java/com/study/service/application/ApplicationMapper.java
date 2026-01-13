package com.study.service.application;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 申请数据访问接口
 */
@Mapper
public interface ApplicationMapper {
    
    /**
     * 获取所有申请
     * @return 申请列表
     */
    List<Application> findAll();
    
    /**
     * 根据ID获取申请
     * @param id 申请ID
     * @return 申请信息
     */
    Application findById(@Param("id") Long id);
    
    /**
     * 根据用户ID获取所有申请
     * @param userId 用户ID
     * @return 申请列表
     */
    List<Application> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据院校ID获取所有申请
     * @param schoolId 院校ID
     * @return 申请列表
     */
    List<Application> findBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * 根据专业ID获取所有申请
     * @param majorId 专业ID
     * @return 申请列表
     */
    List<Application> findByMajorId(@Param("majorId") Long majorId);
    
    /**
     * 根据申请状态获取所有申请
     * @param status 申请状态
     * @return 申请列表
     */
    List<Application> findByStatus(@Param("status") String status);
    
    /**
     * 新增申请
     * @param application 申请信息
     * @return 影响行数
     */
    int insert(Application application);
    
    /**
     * 更新申请信息
     * @param application 申请信息
     * @return 影响行数
     */
    int update(Application application);
    
    /**
     * 删除申请
     * @param id 申请ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
} 