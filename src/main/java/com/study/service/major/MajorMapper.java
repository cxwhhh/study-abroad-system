package com.study.service.major;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 专业数据访问接口
 */
@Mapper
public interface MajorMapper {
    
    /**
     * 获取所有专业
     * @return 专业列表
     */
    List<Major> findAll();
    
    /**
     * 根据ID获取专业
     * @param id 专业ID
     * @return 专业信息
     */
    Major findById(@Param("id") Long id);
    
    /**
     * 根据学院ID获取所有专业
     * @param facultyId 学院ID
     * @return 专业列表
     */
    List<Major> findByFacultyId(@Param("facultyId") Long facultyId);
    
    /**
     * 根据院校ID获取所有专业
     * @param schoolId 院校ID
     * @return 专业列表
     */
    List<Major> findBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * 新增专业
     * @param major 专业信息
     * @return 影响行数
     */
    int insert(Major major);
    
    /**
     * 更新专业信息
     * @param major 专业信息
     * @return 影响行数
     */
    int update(Major major);
    
    /**
     * 删除专业
     * @param id 专业ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
} 
