package com.study.service.adminuser;

import java.util.List;

import org.springframework.data.domain.Page;

import com.study.dto.StatisticsDTO;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 获取管理员用户统计信息
     * @return 统计信息
     */
    StatisticsDTO getStatistics();
    
    /**
     * 根据条件筛选管理员用户列表
     * @param role 角色
     * @param status 状态
     * @param search 搜索关键字
     * @param page 页码
     * @param size 每页大小
     * @return 分页管理员用户列表
     */
    Page<AdminUser> filterAdmins(String role, String status, String search, int page, int size);
    
    /**
     * 添加管理员用户
     * @param adminUserDTO 管理员用户数据
     * @return 添加成功返回用户ID，失败返回null
     */
    Long addAdmin(AdminUserDTO adminUserDTO);
    
    /**
     * 更新管理员用户信息
     * @param adminUserDTO 管理员用户数据
     * @return 更新成功返回true，失败返回false
     */
    boolean updateAdmin(AdminUserDTO adminUserDTO);
    
    /**
     * 删除管理员用户
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteAdmin(Long id);
    
    /**
     * 批量更新管理员用户状态
     * @param ids 用户ID列表
     * @param status 状态值
     * @return 更新成功的记录数
     */
    int batchUpdateStatus(List<Long> ids, int status);
    
    /**
     * 根据ID获取管理员用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    AdminUser getAdminById(Long id);
    
    /**
     * 根据用户名获取管理员用户信息
     * @param username 用户名
     * @return 用户信息
     */
    AdminUser getAdminByUsername(String username);
    
    /**
     * 更新用户最后登录时间和IP
     * @param id 用户ID
     * @param ip 登录IP
     * @return 是否更新成功
     */
    boolean updateLastLogin(Long id, String ip);
} 