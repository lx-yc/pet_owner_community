package com.example.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLoginVO {

    /**
     * 用户token（登录凭证）
     */
    private String token;

    // 用户基本信息
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
    private String signature;

    private Integer level;
    private Integer points;
    private Integer followCount;
    private Integer fansCount;
    private Integer likeCount;

    /** 当前情绪（开心/焦虑/低落） */
    private String moodStatus;
    /** 心理标签 */
    private String mentalTag;
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
}
