package com.example.dto.user;

import lombok.Data;

/**
 * 管理员重置用户密码
 */
@Data
public class AdminResetPwdDTO {
    private Long userId;         // 用户ID
    private String newPassword;  // 新密码
}
