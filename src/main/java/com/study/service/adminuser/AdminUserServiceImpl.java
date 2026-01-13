package com.study.service.adminuser;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.study.dto.StatisticsDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * 管理员用户服务实现类
 */
@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public StatisticsDTO getStatistics() {
        // 初始化统计DTO
        StatisticsDTO statistics = new StatisticsDTO();
        
        try {
            // 基本统计
            long totalUsers = adminUserRepository.count();
            log.info("总用户数: {}", totalUsers);
            
            // 按状态统计
            long activeUsers = adminUserRepository.countByStatus(1);
            long inactiveUsers = adminUserRepository.countByStatus(0);
            log.info("活跃用户数: {}, 未激活用户数: {}", activeUsers, inactiveUsers);
            
            // 检查数据库中所有存在的角色
            try {
                List<String> allRoles = adminUserRepository.findDistinctRoles();
                log.info("数据库中存在的所有角色: {}", allRoles);
                
                // 检查角色数量分布
                log.info("开始检查角色数量分布...");
                if (allRoles != null && !allRoles.isEmpty()) {
                    for (String role : allRoles) {
                        long count = adminUserRepository.countByRole(role);
                        log.info("角色 [{}] 的用户数量: {}", role, count);
                    }
                } else {
                    log.warn("数据库中没有找到任何角色");
                }
            } catch (Exception e) {
                log.warn("查询所有角色失败: {}", e.getMessage());
            }
            
            // 按角色统计 - 添加更详细的日志
            log.info("开始按角色统计用户数量...");
            
            // 超级管理员：包含新旧格式
            long superAdminUsers = adminUserRepository.countByRole("super_admin") + adminUserRepository.countByRole("superAdmin");
            log.info("超级管理员用户数: {}", superAdminUsers);
            
            // 普通管理员：包含新旧格式
            long normalAdminUsers = adminUserRepository.countByRole("normal_admin") + adminUserRepository.countByRole("normalAdmin");
            log.info("普通管理员用户数: {}", normalAdminUsers);
            
            // 咨询顾问（格式保持不变）
            long consultantUsers = adminUserRepository.countByRole("consultant");
            log.info("咨询顾问用户数: {}", consultantUsers);
            
            // 财务人员（格式保持不变）
            long financialUsers = adminUserRepository.countByRole("financial");
            log.info("财务人员用户数: {}", financialUsers);
            
            // 数据分析师：包含新旧格式
            long dataAnalystUsers = adminUserRepository.countByRole("data_analyst") + adminUserRepository.countByRole("dataAnalyst");
            log.info("数据分析师用户数: {}", dataAnalystUsers);
        
            // 最近活跃用户（示例：一周内登录过的用户）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            Date oneWeekAgo = calendar.getTime();
            long recentlyActiveUsers = adminUserRepository.countByLastLoginTimeAfter(oneWeekAgo);
            log.info("最近一周活跃用户数: {}", recentlyActiveUsers);
            
            log.info("角色统计完成 - 超级管理员: {}, 普通管理员: {}, 咨询顾问: {}, 财务人员: {}, 数据分析师: {}", 
                    superAdminUsers, normalAdminUsers, consultantUsers, financialUsers, dataAnalystUsers);
            
            // 设置所有字段
            statistics.setTotalUsers(totalUsers);
            statistics.setActiveUsers(activeUsers);
            statistics.setInactiveUsers(inactiveUsers);
            statistics.setSuperAdminUsers(superAdminUsers);
            statistics.setNormalAdminUsers(normalAdminUsers);
            statistics.setConsultantUsers(consultantUsers);
            statistics.setFinancialUsers(financialUsers);
            statistics.setDataAnalystUsers(dataAnalystUsers);

            
            // 添加日志，输出最终统计结果
            log.info("最终统计结果: {}", statistics);
            
        } catch (Exception e) {
            log.error("获取统计数据时发生错误", e);
            // 异常情况下仍然返回已填充的统计对象，未成功统计的字段将保持默认值0
        }
        
        return statistics;
    }

    @Override
    public Page<AdminUser> filterAdmins(String role, String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 判断是否需要按角色和状态筛选
        if (StringUtils.hasText(role) && StringUtils.hasText(status)) {
            int statusValue = Integer.parseInt(status);
            return adminUserRepository.findByRoleAndStatus(role, statusValue, pageable);
        } 
        // 只按角色筛选
        else if (StringUtils.hasText(role)) {
            return adminUserRepository.findByRole(role, pageable);
        } 
        // 只按状态筛选
        else if (StringUtils.hasText(status)) {
            int statusValue = Integer.parseInt(status);
            return adminUserRepository.findByStatus(statusValue, pageable);
        }
        // 按关键字搜索
        else if (StringUtils.hasText(search)) {
            return adminUserRepository.findByKeyword(search, pageable);
        }
        // 不筛选，返回所有
        else {
            return adminUserRepository.findAll(pageable);
        }
    }

    @Transactional
    @Override
    public Long addAdmin(AdminUserDTO adminUserDTO) {
        try {
            // 检查用户名是否已存在
            if (adminUserRepository.existsByUsername(adminUserDTO.getUsername())) {
                log.error("用户名 {} 已存在", adminUserDTO.getUsername());
                return null;
            }
            
            // 检查邮箱是否已存在（如果提供了邮箱）
            if (StringUtils.hasText(adminUserDTO.getEmail()) && 
                adminUserRepository.existsByEmail(adminUserDTO.getEmail())) {
                log.error("邮箱 {} 已存在", adminUserDTO.getEmail());
                return null;
            }
            
            AdminUser adminUser = new AdminUser();
            BeanUtils.copyProperties(adminUserDTO, adminUser);
            
            // 设置默认值
            Date now = new Date();
            adminUser.setCreateTime(now);
            adminUser.setUpdateTime(now);
            adminUser.setCreatedAt(now);
            adminUser.setUpdatedAt(now);
            
            // 如果状态为空，默认为未激活
            if (adminUser.getStatus() == null) {
                adminUser.setStatus(0);
            }
            
            AdminUser savedUser = adminUserRepository.save(adminUser);
            return savedUser.getId();
        } catch (Exception e) {
            log.error("添加管理员用户失败", e);
            return null;
        }
    }

    @Transactional
    @Override
    public boolean updateAdmin(AdminUserDTO adminUserDTO) {
        try {
            Long adminId = adminUserDTO.getId();
            
            // 检查用户是否存在
            AdminUser existingUser = adminUserRepository.findById(adminId).orElse(null);
            if (existingUser == null) {
                log.error("用户ID {} 不存在", adminId);
                return false;
            }
            
            // 检查邮箱是否已被其他用户使用
            if (StringUtils.hasText(adminUserDTO.getEmail()) && 
                !adminUserDTO.getEmail().equals(existingUser.getEmail()) && 
                adminUserRepository.existsByEmail(adminUserDTO.getEmail())) {
                log.error("邮箱 {} 已被其他用户使用", adminUserDTO.getEmail());
                return false;
            }
            
            // 更新字段，保留不需要更新的字段
            if (StringUtils.hasText(adminUserDTO.getName())) {
                existingUser.setName(adminUserDTO.getName());
            }
            
            if (StringUtils.hasText(adminUserDTO.getEmail())) {
                existingUser.setEmail(adminUserDTO.getEmail());
            }
            
            if (StringUtils.hasText(adminUserDTO.getPhone())) {
                existingUser.setPhone(adminUserDTO.getPhone());
            }
            
            if (StringUtils.hasText(adminUserDTO.getRole())) {
                existingUser.setRole(adminUserDTO.getRole());
            }
            
            if (adminUserDTO.getStatus() != null) {
                existingUser.setStatus(adminUserDTO.getStatus());
            }
            
            if (StringUtils.hasText(adminUserDTO.getNickname())) {
                existingUser.setNickname(adminUserDTO.getNickname());
            }
            
            // 更新时间
            Date now = new Date();
            existingUser.setUpdateTime(now);
            existingUser.setUpdatedAt(now);
            
            adminUserRepository.save(existingUser);
            return true;
        } catch (Exception e) {
            log.error("更新管理员用户失败", e);
            return false;
        }
    }

    @Transactional
    @Override
    public boolean deleteAdmin(Long id) {
        try {
            // 检查用户是否存在
            if (!adminUserRepository.existsById(id)) {
                log.error("用户ID {} 不存在", id);
                return false;
            }
            
            adminUserRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("删除管理员用户失败", e);
            return false;
        }
    }

    @Transactional
    @Override
    public int batchUpdateStatus(List<Long> ids, int status) {
        try {
            return adminUserRepository.updateStatusByIds(ids, status);
        } catch (Exception e) {
            log.error("批量更新管理员用户状态失败", e);
            return 0;
        }
    }

    @Override
    public AdminUser getAdminById(Long id) {
        try {
            return adminUserRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("获取管理员用户失败", e);
            return null;
        }
    }

    @Override
    public AdminUser getAdminByUsername(String username) {
        try {
            return adminUserRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("根据用户名获取管理员用户失败", e);
            return null;
        }
    }

    /**
     * 更新用户最后登录时间和IP
     * @param id 用户ID
     * @param ip 登录IP
     * @return 是否更新成功
     */
    @Transactional
    @Override
    public boolean updateLastLogin(Long id, String ip) {
        try {
            AdminUser adminUser = adminUserRepository.findById(id).orElse(null);
            if (adminUser == null) {
                log.error("用户ID {} 不存在，无法更新登录信息", id);
                return false;
            }
            
            Date now = new Date();
            adminUser.setLastLoginTime(now);
            adminUser.setLastLoginIp(ip);
            adminUser.setUpdateTime(now);
            adminUser.setUpdatedAt(now);
            
            // 如果状态为未激活，登录后自动激活
            if (adminUser.getStatus() != null && adminUser.getStatus() == 0) {
                adminUser.setStatus(1);
                log.info("用户 {} 首次登录，状态已从未激活更新为活跃", adminUser.getUsername());
            }
            
            adminUserRepository.save(adminUser);
            log.info("已更新用户 {} 的登录信息", adminUser.getUsername());
            return true;
        } catch (Exception e) {
            log.error("更新用户登录信息失败", e);
            return false;
        }
    }
} 