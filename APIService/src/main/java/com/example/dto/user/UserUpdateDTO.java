package com.example.dto.user;

import lombok.Data;

@Data
public class UserUpdateDTO {
//    private Long id;             // 用户ID（必须）
    private String nickname;     // 昵称
    private String avatar;       //头像
    private String signature;    // 签名
    private String phone;        // 手机号
    private String email;        // 邮箱
    private String moodStatus;   // 情绪
    private String mentalTag;    // 心理标签
}
