package com.study.service.consultation;

import org.apache.ibatis.annotations.Mapper;

/**
 * 咨询信息数据访问接口
 */
@Mapper
public interface ConsultationMapper {
    /**
     * 插入新的咨询记录
     *
     * @param consultation 咨询信息
     * @return 影响的行数
     */
    int insert(Consultation consultation);

    /**
     * 根据ID查询咨询记录
     *
     * @param id 咨询ID
     * @return 咨询信息
     */
    Consultation selectById(Long id);
} 