package com.study.service.consultation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 咨询信息实体类
 * 用于存储用户的咨询请求信息
 */
@Data
public class Consultation {
    /** 咨询ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 学生ID */
    private Long studentId;
    /** 咨询类型 */
    private String consultationType;
    /** 咨询内容 */
    private String consultationContent;
    /** 处理状态：pending-待处理，processing-处理中，completed-已完成 */
    private String status;
    
    /** 咨询时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consultationTime;
    
    /** 回复 */
    private String reply;
    
    /** 回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;
    
    /** 咨询师姓名 */
    private String consultantName;
    /** 联系方式 */
    private String contactMethod;
    /** 联系信息 */
    private String contactInfo;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    // 关联的学生信息
    /** 学生姓名 */
    private String studentName;
    /** 学生邮箱 */
    private String studentEmail;
    /** 学生电话 */
    private String studentPhone;

    // 关联的用户信息
    /** 用户姓名 */
    private String userName;
    /** 用户邮箱 */
    private String userEmail;
} 