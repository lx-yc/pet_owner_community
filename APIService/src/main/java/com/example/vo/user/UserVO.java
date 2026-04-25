package com.example.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    // 用户基本信息
    private String nickname;
    private String avatar;
    private String signature;

    private String role;
    private Integer status;

    private Integer followCount;
    private Integer fansCount;
    private Integer likeCount;


    /** 当前情绪（开心/焦虑/低落） */
    private String moodStatus;
    /** 心理标签 */
    private String mentalTag;

    /** 当前用户是否已关注该用户 */
    private Boolean isFollowed;
    
    /** 是否互相关注 */
    private Boolean isMutualFollow;
}
