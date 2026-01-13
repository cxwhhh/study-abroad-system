package com.study.service.consultation;

import com.study.common.Result;

import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 咨询管理控制器
 */
@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    @Resource
    private ConsultationService consultationService;

    /**
     * 提交咨询
     *
     * @param consultation 咨询信息
     * @return 处理结果
     */
    @PostMapping
    public Result<?> createConsultation(@RequestBody Consultation consultation) {
        int result = consultationService.createConsultation(consultation);
        if (result > 0) {
            return Result.success(consultation);
        }
        return Result.error("提交失败");
    }

    /**
     * 获取咨询详情
     *
     * @param id 咨询ID
     * @return 咨询信息
     */
    @GetMapping("/{id}")
    public Result<Consultation> getConsultation(@PathVariable Long id) {
        Consultation consultation = consultationService.getConsultationById(id);
        return Result.success(consultation);
    }
}
