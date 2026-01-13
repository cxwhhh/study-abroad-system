package com.study.service.password.exception;

/**
 * 请求过于频繁异常
 */
public class TooManyAttemptsException extends Exception {
    
    public TooManyAttemptsException(String message) {
        super(message);
    }
    
    public TooManyAttemptsException(String message, Throwable cause) {
        super(message, cause);
    }
} 