package com.study.service.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务，用于发送验证码邮件
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送密码重置验证码
     * @param toEmail 目标邮箱
     * @param code 验证码
     */
    public void sendPasswordResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【新英美留学】密码重置验证码");
        message.setText("您好，\n\n您的密码重置验证码为: " + code + "，有效期10分钟。\n\n如非本人操作，请忽略此邮件。\n\n新英美留学团队");
        
        try {
            System.out.println("正在发送邮件到: " + toEmail);
            mailSender.send(message);
            System.out.println("邮件发送成功!");
        } catch (Exception e) {
            // 记录日志
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈信息
            throw new RuntimeException("发送验证码邮件失败", e);
        }
    }
} 