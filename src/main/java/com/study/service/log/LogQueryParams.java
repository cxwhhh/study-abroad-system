package com.study.service.log;

import java.util.List;

import lombok.Data;

/**
 * 日志查询参数对象
 */
@Data
public class LogQueryParams {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页记录数
     */
    private Integer pageSize = 10;

    /**
     * 操作类型（1=登录日志，2=操作日志，3=系统日志，4=错误日志，5=其他日志）
     */
    private String operType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 状态（0=正常，1=异常）
     */
    private String status;

    /**
     * 操作开始时间
     */
    private String beginTime;

    /**
     * 操作结束时间
     */
    private String endTime;

    /**
     * 学生ID (用于学生信息日志)
     */
    private Long studentId;

    /**
     * 页面模块 (用于主页操作日志)
     */
    private String pageModule;
    
    /**
     * 日志类型 (all/student/homepage)
     */
    private String logType;
    
    /**
     * 模块列表，用于筛选特定模块的日志
     */
    private List<String> modules;
} 