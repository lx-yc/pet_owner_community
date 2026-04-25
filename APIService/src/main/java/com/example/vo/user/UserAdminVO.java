package com.example.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAdminVO {
    private Long id;
    private String username;     // 用户名
    private String nickname;     // 昵称
    private String avatar;       // 头像
    private String signature;    // 签名
    private String phone;        // 手机号
    private String email;        // 邮箱
    private String role;         // 角色 user/admin
    private Integer status;      // 状态 1正常 0禁用
    private Integer level;       // 等级
    private Integer points;      // 积分
    private Integer followCount; // 关注数
    private Integer fansCount;   // 粉丝数
    private Integer likeCount;   // 获赞数
    private String moodStatus;   // 情绪
    private String mentalTag;    // 心理标签
    private LocalDateTime lastLoginTime; // 最后登录时间
    private String lastLoginIp;  // 最后登录IP
    private LocalDateTime createTime; // 创建时间
}
