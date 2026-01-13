package com.study.service.consultation;

/**
 * 咨询服务接口
 * 处理用户咨询相关的业务逻辑
 */
public interface ConsultationService {
    /**
     * 创建新的咨询记录
     *
     * @param consultation 咨询信息
     * @return 影响的行数
     */
    int createConsultation(Consultation consultation);

    /**
     * 根据ID获取咨询记录
     *
     * @param id 咨询ID
     * @return 咨询信息
     */
    Consultation getConsultationById(Long id);
} 