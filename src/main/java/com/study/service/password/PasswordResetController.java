package com.study.service.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.study.service.password.exception.InvalidTokenException;
import com.study.service.password.exception.InvalidVerificationCodeException;
import com.study.service.password.exception.TooManyAttemptsException;
import com.study.service.password.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理密码重置相关的请求
 */
@RestController
@RequestMapping("/api/user")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;
    
    /**
     * 验证账号并发送验证码
     */
    @PostMapping("/forgot-password/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestBody PasswordResetDTO.AccountVerifyRequest request) {
        try {
            // 检查是否提供了邮箱
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                // 查找用户并发送验证码（需验证邮箱匹配）
                passwordResetService.sendVerificationCode(request.getUsername(), request.getEmail());
            } else {
                // 查找用户并发送验证码（传统方式）
                passwordResetService.sendVerificationCode(request.getUsername());
            }
            return ResponseEntity.ok(new ApiResponse(200, "验证码已发送到您的邮箱", null));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(500, "发送验证码失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 重新发送验证码
     */
    @PostMapping("/forgot-password/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody PasswordResetDTO.AccountVerifyRequest request) {
        try {
            // 检查是否提供了邮箱
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                // 重新生成并发送验证码（需验证邮箱匹配）
                passwordResetService.resendVerificationCode(request.getUsername(), request.getEmail());
            } else {
                // 重新生成并发送验证码（传统方式）
                passwordResetService.resendVerificationCode(request.getUsername());
            }
            return ResponseEntity.ok(new ApiResponse(200, "验证码已重新发送", null));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        } catch (TooManyAttemptsException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(429, "请求过于频繁，请稍后再试", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(500, "发送验证码失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 验证验证码
     */
    @PostMapping("/forgot-password/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody PasswordResetDTO.CodeVerifyRequest request) {
        try {
            // 验证验证码
            String token = passwordResetService.verifyCode(request.getUsername(), request.getCode());
            
            // 如果验证成功，返回重置令牌
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            
            return ResponseEntity.ok(new ApiResponse(200, "验证码验证成功", data));
        } catch (InvalidVerificationCodeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, "验证码错误或已过期", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(500, "验证失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO.ResetRequest request) {
        try {
            // 验证令牌并重置密码
            passwordResetService.resetPassword(request.getUsername(), request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(200, "密码重置成功", null));
        } catch (InvalidTokenException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, "重置令牌无效或已过期", null));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, "账号不存在", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(500, "密码重置失败: " + e.getMessage(), null));
        }
    }
} 