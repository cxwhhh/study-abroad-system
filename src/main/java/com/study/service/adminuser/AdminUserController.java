package com.study.service.adminuser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.common.Result;
import com.study.dto.StatisticsDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 管理员用户控制器
 */
@RestController
@RequestMapping("/api/adminuser")
@Slf4j
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            StatisticsDTO statistics = adminUserService.getStatistics();
            
            // 创建一个有序Map，确保所有字段都被包含在响应中
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("totalUsers", statistics.getTotalUsers());
            statsMap.put("activeUsers", statistics.getActiveUsers());
            statsMap.put("inactiveUsers", statistics.getInactiveUsers());
            statsMap.put("superAdminUsers", statistics.getSuperAdminUsers());
            statsMap.put("normalAdminUsers", statistics.getNormalAdminUsers());
            statsMap.put("consultantUsers", statistics.getConsultantUsers());
            statsMap.put("financialUsers", statistics.getFinancialUsers());
            statsMap.put("dataAnalystUsers", statistics.getDataAnalystUsers());
            
            log.info("返回统计数据: {}", statsMap);
            
            return Result.success(statsMap, "获取统计数据成功");
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据条件筛选用户列表
     */
    @PostMapping("/filter")
    public Result<Map<String, Object>> filterAdmins(@RequestBody Map<String, Object> params) {
        try {
            String role = params.containsKey("role") ? (String) params.get("role") : "";
            String status = params.containsKey("status") ? String.valueOf(params.get("status")) : "";
            String search = params.containsKey("search") ? (String) params.get("search") : "";
            int page = params.containsKey("page") ? Integer.parseInt(String.valueOf(params.get("page"))) : 1;
            int size = params.containsKey("pageSize") ? Integer.parseInt(String.valueOf(params.get("pageSize"))) : 10;
            
            Page<AdminUser> adminsPage = adminUserService.filterAdmins(role, status, search, page, size);
            
            Map<String, Object> result = Map.of(
                "data", adminsPage.getContent(),
                "total", adminsPage.getTotalElements(),
                "page", page,
                "pageSize", size
            );
            
            return Result.success(result, "获取用户列表成功");
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加管理员用户
     */
    @PostMapping("/add")
    public Result<AdminUser> addAdmin(@RequestBody AdminUserDTO adminUserDTO) {
        try {
            // 确保角色字段有效
            if (adminUserDTO.getRole() == null || adminUserDTO.getRole().isEmpty()) {
                return Result.error("角色不能为空");
            }
            
            // 验证角色值是否有效
            if (!isValidRole(adminUserDTO.getRole())) {
                return Result.error("无效的角色值");
            }
            
            Long adminId = adminUserService.addAdmin(adminUserDTO);
            if (adminId != null) {
                AdminUser admin = adminUserService.getAdminById(adminId);
                return Result.success(admin, "添加用户成功");
            } else {
                return Result.error("添加用户失败");
            }
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return Result.error("添加用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新管理员用户信息
     */
    @PostMapping("/update")
    public Result<Boolean> updateAdmin(@RequestBody AdminUserDTO adminUserDTO) {
        try {
            // 确保角色字段有效
            if (adminUserDTO.getRole() == null || adminUserDTO.getRole().isEmpty()) {
                return Result.error("角色不能为空");
            }
            
            // 验证角色值是否有效
            if (!isValidRole(adminUserDTO.getRole())) {
                return Result.error("无效的角色值");
            }
            
            boolean success = adminUserService.updateAdmin(adminUserDTO);
            if (success) {
                return Result.success(true, "更新用户信息成功");
            } else {
                return Result.error("更新用户信息失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证角色是否有效
     * @param role 角色代码
     * @return 是否有效
     */
    private boolean isValidRole(String role) {
        return role.equals("superAdmin") ||
               role.equals("normalAdmin") ||
               role.equals("consultant") ||
               role.equals("financial") ||
               role.equals("dataAnalyst");
    }
    
    /**
     * 删除管理员用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAdmin(@PathVariable Long id) {
        try {
            boolean success = adminUserService.deleteAdmin(id);
            if (success) {
                return Result.success(true, "删除用户成功");
            } else {
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新用户状态
     */
    @PostMapping("/batch-update-status")
    public Result<Integer> batchUpdateStatus(@RequestBody Map<String, Object> params) {
        try {
            List<Long> adminIds = new ArrayList<>();
            if (params.containsKey("adminIds")) {
                if (params.get("adminIds") instanceof List) {
                    adminIds = ((List<?>) params.get("adminIds")).stream()
                            .map(id -> Long.parseLong(String.valueOf(id)))
                            .collect(Collectors.toList());
                } else if (params.get("adminIds") instanceof Long[]) {
                    adminIds = Arrays.asList((Long[]) params.get("adminIds"));
                }
            }
            
            int status = params.containsKey("status") ? Integer.parseInt(String.valueOf(params.get("status"))) : 0;
            
            if (adminIds.isEmpty()) {
                return Result.error("未提供用户ID列表");
            }
            
            int count = adminUserService.batchUpdateStatus(adminIds, status);
            return Result.success(count, "批量更新用户状态成功");
        } catch (Exception e) {
            log.error("批量更新用户状态失败", e);
            return Result.error("批量更新用户状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<AdminUser> getAdminById(@PathVariable Long id) {
        try {
            AdminUser admin = adminUserService.getAdminById(id);
            if (admin != null) {
                return Result.success(admin, "获取用户信息成功");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载导入模板
     */
    @GetMapping("/template/download")
    public Result<String> downloadTemplate() {
        try {
            // 这里实际应该返回文件下载，这里只是示例
            return Result.success("模板下载链接", "获取导入模板成功");
        } catch (Exception e) {
            log.error("获取导入模板失败", e);
            return Result.error("获取导入模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        try {
            AdminUser admin = adminUserService.getAdminByUsername(username);
            return Result.success(admin != null, "检查用户名成功");
        } catch (Exception e) {
            log.error("检查用户名失败", e);
            return Result.error("检查用户名失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户最后登录时间和IP
     */
    @PostMapping("/update-last-login")
    public Result<Boolean> updateLastLogin(@RequestBody Map<String, Object> params) {
        try {
            Long userId = params.containsKey("userId") ? Long.parseLong(String.valueOf(params.get("userId"))) : null;
            String ip = params.containsKey("ip") ? (String) params.get("ip") : null;
            
            if (userId == null) {
                return Result.error("用户ID不能为空");
            }
            
            boolean success = adminUserService.updateLastLogin(userId, ip);
            if (success) {
                return Result.success(true, "更新登录信息成功");
            } else {
                return Result.error("更新登录信息失败");
            }
        } catch (Exception e) {
            log.error("更新登录信息失败", e);
            return Result.error("更新登录信息失败: " + e.getMessage());
        }
    }
}