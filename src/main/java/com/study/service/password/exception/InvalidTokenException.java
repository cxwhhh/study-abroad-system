package com.study.service.password.exception;

/**
 * 重置令牌无效或过期异常
 */
public class InvalidTokenException extends Exception {
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
} 