package com.study.service.major;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 核心课程数据访问接口
 * 
 * 
 */
@Repository
public interface CoreCourseRepository extends JpaRepository<CoreCourse, Long> {
    List<CoreCourse> findByMajorIdAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(Long majorId);
    /**
     * 根据专业ID查找核心课程列表
     * @param majorId 专业ID
     * @return 核心课程列表
     */
    @Query("SELECT c FROM CoreCourse c WHERE c.majorId = :majorId AND c.isDeleted = false AND c.isActive = true ORDER BY c.sortOrder ASC, c.semester ASC")
    List<CoreCourse> findByMajorIdAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAscSemesterAsc(@Param("majorId") Long majorId);

    /**
     * 根据专业ID和课程类型查找核心课程
     * @param majorId 专业ID
     * @param courseType 课程类型
     * @return 核心课程列表
     */
    @Query("SELECT c FROM CoreCourse c WHERE c.majorId = :majorId AND c.courseType = :courseType AND c.isDeleted = false AND c.isActive = true ORDER BY c.sortOrder ASC")
    List<CoreCourse> findByMajorIdAndCourseTypeAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(@Param("majorId") Long majorId, @Param("courseType") String courseType);

    /**
     * 根据专业ID和学期查找核心课程
     * @param majorId 专业ID
     * @param semester 学期
     * @return 核心课程列表
     */
    @Query("SELECT c FROM CoreCourse c WHERE c.majorId = :majorId AND c.semester = :semester AND c.isDeleted = false AND c.isActive = true ORDER BY c.sortOrder ASC")
    List<CoreCourse> findByMajorIdAndSemesterAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(@Param("majorId") Long majorId, @Param("semester") Integer semester);

    /**
     * 统计专业的核心课程数量
     * @param majorId 专业ID
     * @return 课程数量
     */
    @Query("SELECT COUNT(c) FROM CoreCourse c WHERE c.majorId = :majorId AND c.isDeleted = false AND c.isActive = true")
    Long countByMajorIdAndIsDeletedFalseAndIsActiveTrue(@Param("majorId") Long majorId);
}