package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    /** 用户ID */
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 密码（加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 个性签名 */
    private String signature;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色：user/admin */
    private String role;

    /** 状态：1正常 0禁用 */
    private Integer status;

    /** 用户等级 */
    private Integer level;

    /** 积分 */
    private Integer points;

    /** 关注数 */
    private Integer followCount;

    /** 粉丝数 */
    private Integer fansCount;

    /** 获赞总数 */
    private Integer likeCount;

    /** 当前情绪（开心/焦虑/低落） */
    private String moodStatus;

    /** 心理标签 */
    private String mentalTag;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}