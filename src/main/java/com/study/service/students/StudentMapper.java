package com.study.service.students;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    Student getStudentByUserId(@Param("userId") Long userId);
    int updateStudent(Student student);
    int insert(Student student);
    
    /**
     * 获取学生GPA信息
     * @param userId 用户ID
     * @return GPA信息
     */
    StudentGPA getStudentGPA(@Param("userId") Long userId);
    
    /**
     * 获取所有学生列表
     * @return 学生列表
     */
    List<Student> getAllStudents();
    
    /**
     * 根据ID删除学生
     * @param id 学生ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID获取学生信息
     * @param id 学生ID
     * @return 学生信息
     */
    Student getStudentById(@Param("id") Long id);
} 