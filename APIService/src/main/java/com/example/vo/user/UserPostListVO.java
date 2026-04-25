package com.example.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPostListVO {

    // 用户基本信息
    private Long id;
    private String nickname;
    private String avatar;

}
