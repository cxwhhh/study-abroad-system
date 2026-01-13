package com.study.service.user;

import com.study.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户的ID
     */
    @GetMapping("/current-id")
    public Result<Long> getCurrentUserId(HttpServletRequest request) {
        try {
            // 从session中获取用户信息
            Long userId = userService.getCurrentUserId(request);
            if (userId != null) {
                return Result.success(userId, "获取用户ID成功");
            } else {
                return Result.error("用户未登录");
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户的完整信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        try {
            User user = userService.getCurrentUser(request);
            if (user != null) {
                return Result.success(user, "获取当前用户信息成功");
            } else {
                return Result.error("用户未登录");
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<User> getUserById(@PathVariable Long userId) {
        try {
            log.info("获取用户ID为 {} 的用户信息", userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                return Result.success(user, "获取用户信息成功");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }
}