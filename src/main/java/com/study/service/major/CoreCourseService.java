package com.study.service.major;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 核心课程服务类
 */
@Service
@Transactional
public class CoreCourseService {

    @Autowired
    private CoreCourseRepository coreCourseRepository;

    /**
     * 根据专业ID获取核心课程列表
     * @param majorId 专业ID
     * @return 核心课程列表
     */
    public List<CoreCourse> getCoreCoursesByMajorId(Long majorId) {
        return coreCourseRepository.findByMajorIdAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAscSemesterAsc(majorId);
    }

    /**
     * 根据专业ID和课程类型获取核心课程
     * @param majorId 专业ID
     * @param courseType 课程类型（必修/选修）
     * @return 核心课程列表
     */
    public List<CoreCourse> getCoreCoursesByMajorIdAndType(Long majorId, String courseType) {
        return coreCourseRepository.findByMajorIdAndCourseTypeAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(majorId, courseType);
    }

    /**
     * 根据专业ID和学期获取核心课程
     * @param majorId 专业ID
     * @param semester 学期
     * @return 核心课程列表
     */
    public List<CoreCourse> getCoreCoursesByMajorIdAndSemester(Long majorId, Integer semester) {
        return coreCourseRepository.findByMajorIdAndSemesterAndIsDeletedFalseAndIsActiveTrueOrderBySortOrderAsc(majorId, semester);
    }

    /**
     * 根据ID获取核心课程详情
     * @param id 课程ID
     * @return 核心课程
     */
    public Optional<CoreCourse> getCoreCourseById(Long id) {
        return coreCourseRepository.findById(id);
    }

    /**
     * 添加核心课程
     * @param coreCourse 核心课程信息
     * @return 保存后的核心课程
     */
    public CoreCourse addCoreCourse(CoreCourse coreCourse) {
        coreCourse.setCreatedTime(LocalDateTime.now());
        coreCourse.setUpdatedTime(LocalDateTime.now());
        coreCourse.setIsDeleted(false);
        coreCourse.setIsActive(true);
        return coreCourseRepository.save(coreCourse);
    }

    /**
     * 更新核心课程
     * @param coreCourse 核心课程信息
     * @return 更新后的核心课程
     */
    public CoreCourse updateCoreCourse(CoreCourse coreCourse) {
        coreCourse.setUpdatedTime(LocalDateTime.now());
        return coreCourseRepository.save(coreCourse);
    }

    /**
     * 删除核心课程（软删除）
     * @param id 课程ID
     */
    public void deleteCoreCourse(Long id) {
        Optional<CoreCourse> coreCourseOpt = coreCourseRepository.findById(id);
        if (coreCourseOpt.isPresent()) {
            CoreCourse coreCourse = coreCourseOpt.get();
            coreCourse.setIsDeleted(true);
            coreCourse.setUpdatedTime(LocalDateTime.now());
            coreCourseRepository.save(coreCourse);
        }
    }

    /**
     * 统计专业的核心课程数量
     * @param majorId 专业ID
     * @return 课程数量
     */
    public Long countCoreCoursesByMajorId(Long majorId) {
        return coreCourseRepository.countByMajorIdAndIsDeletedFalseAndIsActiveTrue(majorId);
    }

    /**
     * 批量添加核心课程
     * @param coreCourses 核心课程列表
     * @return 保存后的核心课程列表
     */
    public List<CoreCourse> addCoreCourses(List<CoreCourse> coreCourses) {
        LocalDateTime now = LocalDateTime.now();
        coreCourses.forEach(course -> {
            course.setCreatedTime(now);
            course.setUpdatedTime(now);
            course.setIsDeleted(false);
            course.setIsActive(true);
        });
        return coreCourseRepository.saveAll(coreCourses);
    }
}