// AdminController.java
package com.study.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody AdminLoginRequest request,
            HttpServletRequest servletRequest) {

        System.out.println("===== 登录请求信息 =====");
        System.out.println("请求路径: " + servletRequest.getRequestURI());
        System.out.println("请求方法: " + servletRequest.getMethod());
        System.out.println("用户名: " + request.getUsername());

        Map<String, Object> response = new HashMap<>();
        try {
            // 检查请求对象
            if (request == null || request.getUsername() == null) {
                response.put("code", 400);
                response.put("message", "请求数据无效");
                return ResponseEntity.badRequest().body(response);
            }

            AdminDTO admin = adminService.login(request.getUsername(), request.getPassword());

            HttpSession session = servletRequest.getSession();
            session.setAttribute("admin", admin);
            session.setAttribute("adminId", admin.getId() == null ? null : Long.valueOf(admin.getId().longValue()));

            Map<String, Object> data = new HashMap<>();
            data.put("admin", admin);
            data.put("adminId", admin.getId());
            data.put("token", generateToken(admin));

            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈
            response.put("code", 401);
            response.put("message", e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    // 简单的Token生成方法，生产环境应使用更安全的方法
    private String generateToken(AdminDTO admin) {
        return "admin_token_" + admin.getId() + "_" + System.currentTimeMillis();
    }

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("AdminController is working!");
    }

    @GetMapping("/simple-test")
    public String simpleTest() {
        return "AdminController简单测试成功";
    }
}
