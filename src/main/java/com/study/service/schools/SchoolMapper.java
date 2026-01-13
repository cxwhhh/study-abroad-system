package com.study.service.schools;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 院校数据访问接口
 */
@Mapper
public interface SchoolMapper {
    
    /**
     * 获取所有院校
     * @return 院校列表
     */
    List<School> findAll();
    
    /**
     * 根据ID获取院校
     * @param id 院校ID
     * @return 院校信息
     */
    School findById(@Param("id") Long id);
    
    /**
     * 根据国家筛选院校
     * @param country 国家
     * @return 院校列表
     */
    List<School> findByCountry(@Param("country") String country);
    
    /**
     * 根据类型筛选院校
     * @param type 院校类型
     * @return 院校列表
     */
    List<School> findByType(@Param("type") String type);
    
    /**
     * 新增院校
     * @param school 院校信息
     * @return 影响行数
     */
    int insert(School school);
    
    /**
     * 更新院校信息
     * @param school 院校信息
     * @return 影响行数
     */
    int update(School school);
    
    /**
     * 删除院校
     * @param id 院校ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 搜索院校
     * @param keyword 关键词
     * @return 院校列表
     */
    List<School> searchByKeyword(@Param("keyword") String keyword);
} 