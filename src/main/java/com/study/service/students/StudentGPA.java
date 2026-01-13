package com.study.service.students;

import lombok.Data;

@Data
public class StudentGPA {
    private Long userId;
    private String gpaScale;  // GPA制度（4.0、5.0等）
    private Double gpaScore;  // GPA分数
} 