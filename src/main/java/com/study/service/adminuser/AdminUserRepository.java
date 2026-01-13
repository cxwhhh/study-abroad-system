package com.study.service.adminuser;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    AdminUser findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据角色统计用户数量
     * @param role 角色名称
     * @return 用户数量
     */
    long countByRole(String role);
    
    /**
     * 根据状态统计用户数量
     * @param status 状态值
     * @return 用户数量
     */
    long countByStatus(int status);
    
    /**
     * 统计最后登录时间在指定日期之后的用户数量
     * @param date 日期
     * @return 用户数量
     */
    long countByLastLoginTimeAfter(Date date);
    
    /**
     * 根据角色查找用户列表
     * @param role 角色名称
     * @return 用户列表
     */
    List<AdminUser> findByRole(String role);
    
    /**
     * 分页查询指定角色的用户
     * @param role 角色名称
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<AdminUser> findByRole(String role, Pageable pageable);
    
    /**
     * 根据状态查找用户列表
     * @param status 状态值
     * @return 用户列表
     */
    List<AdminUser> findByStatus(int status);
    
    /**
     * 分页查询指定状态的用户
     * @param status 状态值
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<AdminUser> findByStatus(int status, Pageable pageable);
    
    /**
     * 根据角色和状态查找用户列表
     * @param role 角色名称
     * @param status 状态值
     * @return 用户列表
     */
    List<AdminUser> findByRoleAndStatus(String role, int status);
    
    /**
     * 分页查询指定角色和状态的用户
     * @param role 角色名称
     * @param status 状态值
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    Page<AdminUser> findByRoleAndStatus(String role, int status, Pageable pageable);
    
    /**
     * 按照用户名或姓名或邮箱模糊查询
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 分页用户列表
     */
    @Query("SELECT u FROM AdminUser u WHERE u.username LIKE %:keyword% OR u.name LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<AdminUser> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 批量更新用户状态
     * @param ids 用户ID列表
     * @param status 状态值
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE AdminUser u SET u.status = :status WHERE u.id IN :ids")
    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") int status);
    
    /**
     * 查询所有不同的角色
     * @return 角色列表
     */
    @Query("SELECT DISTINCT a.role FROM AdminUser a WHERE a.role IS NOT NULL")
    List<String> findDistinctRoles();
} 