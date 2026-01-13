package com.study.service.user;

import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);
    
    @Insert("INSERT INTO users (username, password, name, email, phone, role, status, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{name}, #{email}, #{phone}, #{role}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET update_time = #{updatedAt} WHERE id = #{id}")
    int updateLastLoginTime(User user);
    
    @Update("UPDATE users SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    @Update("UPDATE users SET email = #{email}, update_time = NOW() WHERE id = #{id}")
    int updateEmail(@Param("id") Long id, @Param("email") String email);
}
