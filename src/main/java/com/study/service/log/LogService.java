package com.study.service.log;

import java.util.List;
import java.util.Map;

/**
 * 日志服务接口
 */
public interface LogService {
    
    /**
     * 保存日志
     * 
     * @param log 日志实体
     * @return 保存结果
     */
    boolean saveLog(Log log);
    
    /**
     * 根据条件分页查询日志列表
     * 
     * @param params 查询参数
     * @return 日志列表和分页信息
     */
    Map<String, Object> getLogsList(LogQueryParams params);
    
    /**
     * 获取日志详情
     * 
     * @param id 日志ID
     * @return 日志详情
     */
    Log getLogDetail(Long id);
    
    /**
     * 查询学生相关日志
     * 
     * @param params 查询参数
     * @return 学生相关日志列表和分页信息
     */
    Map<String, Object> getStudentLogs(LogQueryParams params);
    
    /**
     * 查询主页操作日志
     * 
     * @param params 查询参数
     * @return 主页操作日志列表和分页信息
     */
    Map<String, Object> getHomepageLogs(LogQueryParams params);
    
    /**
     * 清理过期日志
     * 
     * @param days 保留天数
     * @return 清理结果
     */
    boolean cleanLogs(int days);
    
    /**
     * 导出日志
     * 
     * @param params 查询参数
     * @return 导出结果
     */
    byte[] exportLogs(LogQueryParams params);
    
    /**
     * 获取日志类型选项
     * 
     * @return 日志类型列表
     */
    List<Map<String, String>> getLogTypes();
    
    /**
     * 获取系统模块选项
     * 
     * @return 系统模块列表
     */
    List<Map<String, String>> getModules();
} 