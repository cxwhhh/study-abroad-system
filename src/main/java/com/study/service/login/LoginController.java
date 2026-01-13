package com.study.service.login;

import com.study.common.Result;
import com.study.service.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            log.info("登录请求到达，用户名：{}", request.getUsername());
            User user = loginService.login(request.getUsername(), request.getPassword());
            
            HttpSession session = httpRequest.getSession();
            // 设置用户信息到session中
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            
            // 获取并设置学生ID
            Long studentId = loginService.getStudentIdByUserId(user.getId());
            if (studentId != null) {
                session.setAttribute("studentId", studentId);
            }
            
            // 构建登录响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("userId", user.getId());
            
            // 添加学生ID到响应数据
            if (studentId != null) {
                responseData.put("studentId", studentId);
            }
            
            // 获取重定向URL
            String redirectUrl = (String) session.getAttribute("PREVIOUS_PAGE");
            log.info("获取到来源页面：{} | SessionID：{}", redirectUrl, session.getId());
            session.removeAttribute("PREVIOUS_PAGE");
            responseData.put("redirectUrl", redirectUrl);
            
            return Result.success(responseData, "登录成功");

        } catch (Exception e) {
            log.error("登录失败，原因：{}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            // 确保email不为null
            String email = request.getEmail() != null ? request.getEmail() : "";
            log.info("注册请求，用户名: {}，邮箱: {}", request.getUsername(), email);
            
            User user = loginService.register(request.getUsername(), request.getPassword(), email);
            
            // 确保返回的用户数据包含email字段
            log.info("注册成功，用户ID: {}, 用户名: {}, 邮箱: {}", 
                    user.getId(), user.getUsername(), user.getEmail());
            
            return Result.success(user, "注册成功");
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    // 修改记录来源页面接口
    @GetMapping("/record-previous-page")
    public void recordPreviousPage(@RequestParam String url, 
                                  HttpServletRequest request) {
        log.info("记录来源页面：{} | SessionID：{}", url, request.getSession().getId());
        request.getSession().setAttribute("PREVIOUS_PAGE", url);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            log.info("获取用户信息，当前session id: {}", session.getId());
            
            User user = (User) session.getAttribute("user");
            if (user == null) {
                log.warn("用户未登录，session id: {}, session attributes: {}", 
                    session.getId(), 
                    java.util.Collections.list(session.getAttributeNames()));
                return Result.error("用户未登录");
            }
            
            // 清除敏感信息
            user.setPassword(null);
            log.info("成功获取用户信息: {}, session id: {}", user.getUsername(), session.getId());
            
            // 返回成功响应
            return Result.success(user, "获取用户信息成功");
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                log.info("用户退出登录，session id: {}", session.getId());
                session.invalidate();
            }
            return Result.success(null, "退出登录成功");
        } catch (Exception e) {
            log.error("退出登录失败", e);
            return Result.error(e.getMessage());
        }
    }
}