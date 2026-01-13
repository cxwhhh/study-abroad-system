package com.study.service.log;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 系统日志数据访问层
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Long>, JpaSpecificationExecutor<Log> {
    
    /**
     * 根据操作类型查询日志
     */
    List<Log> findByOperType(String operType);
    
    /**
     * 根据操作人员查询日志
     */
    List<Log> findByOperName(String operName);
    
    /**
     * 根据操作状态查询日志
     */
    List<Log> findByStatus(String status);
    
    /**
     * 根据操作时间范围查询日志
     */
    List<Log> findByOperTimeBetween(Date beginTime, Date endTime);
    
    /**
     * 根据操作模块查询日志
     */
    List<Log> findByTitle(String title);
    
    /**
     * 根据学生ID查询相关日志
     */
    List<Log> findByStudentId(Long studentId);
    
    /**
     * 根据页面模块查询主页相关操作日志
     */
    List<Log> findByPageModule(String pageModule);
    
    /**
     * 复杂查询：根据多个条件组合查询学生相关日志
     */
    @Query("SELECT l FROM Log l WHERE l.studentId IS NOT NULL " +
           "AND (:operType IS NULL OR l.operType = :operType) " +
           "AND (:operName IS NULL OR l.operName LIKE %:operName%) " +
           "AND (:status IS NULL OR l.status = :status) " +
           "AND (:title IS NULL OR l.title = :title) " +
           "AND (:studentId IS NULL OR l.studentId = :studentId) " +
           "AND ((:beginTime IS NULL AND :endTime IS NULL) OR l.operTime BETWEEN :beginTime AND :endTime)")
    Page<Log> findStudentLogs(
            @Param("operType") String operType,
            @Param("operName") String operName,
            @Param("status") String status,
            @Param("title") String title,
            @Param("studentId") Long studentId,
            @Param("beginTime") Date beginTime,
            @Param("endTime") Date endTime,
            Pageable pageable);
    
    /**
     * 复杂查询：根据多个条件组合查询主页相关操作日志
     */
    @Query("SELECT l FROM Log l WHERE l.pageModule IS NOT NULL " +
           "AND (:operType IS NULL OR l.operType = :operType) " +
           "AND (:operName IS NULL OR l.operName LIKE %:operName%) " +
           "AND (:status IS NULL OR l.status = :status) " +
           "AND (:pageModule IS NULL OR l.pageModule = :pageModule) " +
           "AND ((:beginTime IS NULL AND :endTime IS NULL) OR l.operTime BETWEEN :beginTime AND :endTime)")
    Page<Log> findHomepageLogs(
            @Param("operType") String operType,
            @Param("operName") String operName,
            @Param("status") String status,
            @Param("pageModule") String pageModule,
            @Param("beginTime") Date beginTime,
            @Param("endTime") Date endTime,
            Pageable pageable);
} 