package com.study.service.password.exception;

/**
 * 用户未找到异常
 */
public class UserNotFoundException extends Exception {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 