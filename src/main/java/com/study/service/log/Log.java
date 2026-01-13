package com.study.service.log;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 系统日志实体类
 */
@Data
@Entity
@Table(name = "sys_log")
public class Log {

    /**
     * 日志主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operId;

    /**
     * 操作模块
     */
    @Column(length = 50)
    private String title;

    /**
     * 业务类型（1=登录日志，2=操作日志，3=系统日志，4=错误日志，5=其他日志）
     */
    @Column(length = 2)
    private String operType;

    /**
     * 操作人员ID
     */
    @Column
    private Long operUserId;
    
    /**
     * 操作人员
     */
    @Column(length = 50)
    private String operName;

    /**
     * 操作地址
     */
    @Column(length = 128)
    private String operIp;

    /**
     * 操作地点
     */
    @Column(length = 255)
    private String operLocation;

    /**
     * 请求方法
     */
    @Column(length = 255)
    private String method;

    /**
     * 请求URL
     */
    @Column(length = 255)
    private String operUrl;

    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String operParam;

    /**
     * 返回结果
     */
    @Column(columnDefinition = "TEXT")
    private String jsonResult;

    /**
     * 操作状态（0=正常，1=异常）
     */
    @Column(length = 1)
    private String status;

    /**
     * 错误消息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Column
    private Date operTime;
    
    /**
     * 学生ID (用于学生相关日志)
     */
    @Column
    private Long studentId;
    
    /**
     * 学生姓名 (用于学生相关日志)
     */
    @Column(length = 100)
    private String studentName;
    
    /**
     * 页面模块 (用于主页操作日志)
     */
    @Column(length = 100)
    private String pageModule;
    
    /**
     * 动作类型 (如查看、编辑、删除等)
     */
    @Column(length = 50)
    private String actionType;
} 