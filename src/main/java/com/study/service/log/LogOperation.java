package com.study.service.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型（1=登录日志，2=操作日志，3=系统日志，4=错误日志，5=其他日志）
     */
    String operType() default "2";
    
    /**
     * 操作说明
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;
    
    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;
} 