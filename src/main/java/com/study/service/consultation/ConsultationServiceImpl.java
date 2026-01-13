package com.study.service.consultation;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 咨询服务实现类
 */
@Service
public class ConsultationServiceImpl implements ConsultationService {
    
    @Resource
    private ConsultationMapper consultationMapper;

    @Override
    public int createConsultation(Consultation consultation) {
        // 设置初始状态和创建时间
        consultation.setStatus("待回复");
        consultation.setCreateTime(new Date());
        consultation.setUpdateTime(new Date());
        return consultationMapper.insert(consultation);
    }

    @Override
    public Consultation getConsultationById(Long id) {
        return consultationMapper.selectById(id);
    }
} 