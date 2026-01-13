package com.study.service.password.exception;

/**
 * 验证码无效或过期异常
 */
public class InvalidVerificationCodeException extends Exception {
    
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
    
    public InvalidVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }
} 