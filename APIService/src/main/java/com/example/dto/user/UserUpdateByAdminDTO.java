package com.example.dto.user;

import lombok.Data;

/**
 * 管理员修改用户信息
 */
@Data
public class UserUpdateByAdminDTO {
//    private Long id;             // 用户ID（必须）
    private String nickname;     // 昵称
    private String signature;    // 签名
    private String phone;        // 手机号
    private String email;        // 邮箱
    private String role;         // 角色：user/admin
    private Integer status;      // 状态：1正常 0禁用
    private Integer level;       // 等级
    private Integer points;      // 积分
    private String moodStatus;   // 情绪
    private String mentalTag;    // 心理标签
}
