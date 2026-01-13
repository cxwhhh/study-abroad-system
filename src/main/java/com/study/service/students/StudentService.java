package com.study.service.students;

import java.util.List;

public interface StudentService {
    /**
     * 根据用户ID获取学生信息
     * @param userId 用户ID
     * @return 学生信息
     */
    Student getStudentByUserId(Long userId);

    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 更新是否成功
     */
    boolean updateStudent(Student student);

    /**
     * 同步学生信息
     * @param student 学生信息
     * @return 更新后的学生信息
     */
    Student syncStudentInfo(Student student);

    /**
     * 获取学生GPA信息
     * @param userId 用户ID
     * @return 学生GPA信息
     */
    StudentGPA getStudentGPA(Long userId);
    
    /**
     * 获取所有学生列表（管理员使用）
     * @return 学生列表
     */
    List<Student> getAllStudents();
    
    /**
     * 添加新学生（管理员使用）
     * @param student 学生信息
     * @return 添加后的学生信息
     */
    Student addStudent(Student student);
    
    /**
     * 删除学生（管理员使用）
     * @param id 学生ID
     */
    void deleteStudent(Long id);

    /**
     * 根据ID获取学生信息
     * @param id 学生ID
     * @return 学生信息
     */
    Student getStudentById(Long id);
} 