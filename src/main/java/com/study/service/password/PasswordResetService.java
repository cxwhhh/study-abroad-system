package com.study.service.password;

import com.study.service.password.exception.InvalidTokenException;
import com.study.service.password.exception.InvalidVerificationCodeException;
import com.study.service.password.exception.TooManyAttemptsException;
import com.study.service.password.exception.UserNotFoundException;
import com.study.service.user.User;
import com.study.service.user.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密码重置服务，处理密码重置的核心业务逻辑
 */
@Service
public class PasswordResetService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    // 存储验证码的缓存，生产环境建议使用Redis替代
    private final Map<String, VerificationData> verificationCodes = new ConcurrentHashMap<>();
    
    // 存储重置令牌的缓存，生产环境建议使用Redis替代
    private final Map<String, ResetTokenData> resetTokens = new ConcurrentHashMap<>();
    
    // 验证码有效期（分钟）
    private static final int CODE_EXPIRY_MINUTES = 10;
    
    // 令牌有效期（分钟）
    private static final int TOKEN_EXPIRY_MINUTES = 30;
    
    // 重发验证码间隔（秒）
    private static final int RESEND_INTERVAL_SECONDS = 60;
    
    /**
     * 发送验证码到用户邮箱
     * @param username 用户名
     * @throws UserNotFoundException 如果用户不存在
     */
    public void sendVerificationCode(String username) throws UserNotFoundException {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
        
        // 生成6位随机验证码
        String code = generateRandomCode();
        
        // 保存验证码及相关信息
        LocalDateTime now = LocalDateTime.now();
        VerificationData data = new VerificationData(
            code, 
            now,
            now.plusMinutes(CODE_EXPIRY_MINUTES),
            0
        );
        verificationCodes.put(username, data);
        
        // 发送验证码到用户邮箱
        emailService.sendPasswordResetCode(user.getEmail(), code);
    }
    
    /**
     * 发送验证码到指定邮箱（需验证账号和邮箱匹配）
     * @param username 用户名
     * @param email 邮箱
     * @throws UserNotFoundException 如果用户不存在或邮箱不匹配
     */
    public void sendVerificationCode(String username, String email) throws UserNotFoundException {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
        
        // 验证邮箱是否匹配
        if (user.getEmail() == null || !user.getEmail().equals(email)) {
            throw new UserNotFoundException("账号与邮箱不匹配");
        }
        
        // 生成6位随机验证码
        String code = generateRandomCode();
        
        // 保存验证码及相关信息
        LocalDateTime now = LocalDateTime.now();
        VerificationData data = new VerificationData(
            code, 
            now,
            now.plusMinutes(CODE_EXPIRY_MINUTES),
            0
        );
        verificationCodes.put(username, data);
        
        // 发送验证码到用户邮箱
        emailService.sendPasswordResetCode(email, code);
    }
    
    /**
     * 重新发送验证码
     * @param username 用户名
     * @throws UserNotFoundException 如果用户不存在
     * @throws TooManyAttemptsException 如果请求过于频繁
     */
    public void resendVerificationCode(String username) throws UserNotFoundException, TooManyAttemptsException {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
        
        // 检查是否可以重新发送
        VerificationData existingData = verificationCodes.get(username);
        if (existingData != null) {
            LocalDateTime now = LocalDateTime.now();
            if (existingData.getLastSent().plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(now)) {
                throw new TooManyAttemptsException("请求过于频繁，请稍后再试");
            }
            
            // 更新重试次数
            existingData.setAttempts(existingData.getAttempts() + 1);
            
            // 生成新的验证码并更新
            String newCode = generateRandomCode();
            existingData.setCode(newCode);
            existingData.setLastSent(now);
            existingData.setExpiry(now.plusMinutes(CODE_EXPIRY_MINUTES));
            
            // 发送验证码到用户邮箱
            emailService.sendPasswordResetCode(user.getEmail(), newCode);
        } else {
            // 如果不存在验证码记录，直接发送
            sendVerificationCode(username);
        }
    }
    
    /**
     * 重新发送验证码到指定邮箱
     * @param username 用户名
     * @param email 邮箱
     * @throws UserNotFoundException 如果用户不存在或邮箱不匹配
     * @throws TooManyAttemptsException 如果请求过于频繁
     */
    public void resendVerificationCode(String username, String email) throws UserNotFoundException, TooManyAttemptsException {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
        
        // 验证邮箱是否匹配
        if (user.getEmail() == null || !user.getEmail().equals(email)) {
            throw new UserNotFoundException("账号与邮箱不匹配");
        }
        
        // 检查是否可以重新发送
        VerificationData existingData = verificationCodes.get(username);
        if (existingData != null) {
            LocalDateTime now = LocalDateTime.now();
            if (existingData.getLastSent().plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(now)) {
                throw new TooManyAttemptsException("请求过于频繁，请稍后再试");
            }
            
            // 更新重试次数
            existingData.setAttempts(existingData.getAttempts() + 1);
            
            // 生成新的验证码并更新
            String newCode = generateRandomCode();
            existingData.setCode(newCode);
            existingData.setLastSent(now);
            existingData.setExpiry(now.plusMinutes(CODE_EXPIRY_MINUTES));
            
            // 发送验证码到用户邮箱
            emailService.sendPasswordResetCode(email, newCode);
        } else {
            // 如果不存在验证码记录，直接发送
            sendVerificationCode(username, email);
        }
    }
    
    /**
     * 验证验证码
     * @param username 用户名
     * @param code 用户提交的验证码
     * @return 重置令牌
     * @throws InvalidVerificationCodeException 如果验证码无效或已过期
     */
    public String verifyCode(String username, String code) throws InvalidVerificationCodeException {
        // 获取验证码信息
        VerificationData data = verificationCodes.get(username);
        if (data == null) {
            throw new InvalidVerificationCodeException("未找到验证码记录");
        }
        
        // 检查验证码是否过期
        if (LocalDateTime.now().isAfter(data.getExpiry())) {
            verificationCodes.remove(username);
            throw new InvalidVerificationCodeException("验证码已过期");
        }
        
        // 检查验证码是否匹配
        if (!data.getCode().equals(code)) {
            throw new InvalidVerificationCodeException("验证码错误");
        }
        
        // 验证成功，生成重置令牌
        String token = UUID.randomUUID().toString();
        resetTokens.put(username, new ResetTokenData(token, LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)));
        
        // 移除验证码记录
        verificationCodes.remove(username);
        
        return token;
    }
    
    /**
     * 重置密码
     * @param username 用户名
     * @param token 重置令牌
     * @param newPassword 新密码
     * @throws InvalidTokenException 如果令牌无效或已过期
     * @throws UserNotFoundException 如果用户不存在
     */
    public void resetPassword(String username, String token, String newPassword) 
            throws InvalidTokenException, UserNotFoundException {
        // 验证令牌
        ResetTokenData storedToken = resetTokens.get(username);
        if (storedToken == null || !storedToken.token.equals(token)) {
            throw new InvalidTokenException("重置令牌无效");
        }
        if (LocalDateTime.now().isAfter(storedToken.expiry)) {
            resetTokens.remove(username);
            throw new InvalidTokenException("重置令牌已过期");
        }
        
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
        
        // 更新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(user.getId(), encodedPassword);
        
        // 移除令牌
        resetTokens.remove(username);
    }
    
    /**
     * 生成6位随机验证码
     */
    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * 内部类，用于存储验证码相关信息
     */
    private static class VerificationData {
        private String code;
        private LocalDateTime lastSent;
        private LocalDateTime expiry;
        private int attempts;
        
        public VerificationData(String code, LocalDateTime lastSent, LocalDateTime expiry, int attempts) {
            this.code = code;
            this.lastSent = lastSent;
            this.expiry = expiry;
            this.attempts = attempts;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public LocalDateTime getLastSent() {
            return lastSent;
        }
        
        public void setLastSent(LocalDateTime lastSent) {
            this.lastSent = lastSent;
        }
        
        public LocalDateTime getExpiry() {
            return expiry;
        }
        
        public void setExpiry(LocalDateTime expiry) {
            this.expiry = expiry;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }
    }

    private static class ResetTokenData {
        private final String token;
        private final LocalDateTime expiry;

        private ResetTokenData(String token, LocalDateTime expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }
} 
