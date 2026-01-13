package com.study.service.password;

/**
 * 密码重置相关的数据传输对象
 */
public class PasswordResetDTO {

    /**
     * 账号验证请求DTO
     */
    public static class AccountVerifyRequest {
        private String username;
        private String email;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 验证码验证请求DTO
     */
    public static class CodeVerifyRequest {
        private String username;
        private String code;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    /**
     * 重置密码请求DTO
     */
    public static class ResetRequest {
        private String username;
        private String token;
        private String newPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
} 