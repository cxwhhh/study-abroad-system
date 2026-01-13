package com.study.service.faculty;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 学院数据访问接口
 */
@Mapper
public interface FacultyMapper {
    
    /**
     * 获取所有学院
     * @return 学院列表
     */
    List<Faculty> findAll();
    
    /**
     * 根据ID获取学院
     * @param id 学院ID
     * @return 学院信息
     */
    Faculty findById(@Param("id") Long id);
    
    /**
     * 根据院校ID获取所有学院
     * @param schoolId 院校ID
     * @return 学院列表
     */
    List<Faculty> findBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * 新增学院
     * @param faculty 学院信息
     * @return 影响行数
     */
    int insert(Faculty faculty);
    
    /**
     * 更新学院信息
     * @param faculty 学院信息
     * @return 影响行数
     */
    int update(Faculty faculty);
    
    /**
     * 删除学院
     * @param id 学院ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
} 
