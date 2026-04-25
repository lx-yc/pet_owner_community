package com.example.dto.user;

import lombok.Data;

/**
 * 用户登录请求参数
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（明文，后端会加密校验）
     */
    private String password;
}