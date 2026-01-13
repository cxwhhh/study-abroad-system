package com.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 留学咨询系统启动类
 * 
 * @author cxw
 * @since 2025-01-16
 */
@SpringBootApplication
@MapperScan(value = {"com.study.mapper", "com.study.service"}, 
           annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class StudyAbroadApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyAbroadApplication.class, args);
    }
}