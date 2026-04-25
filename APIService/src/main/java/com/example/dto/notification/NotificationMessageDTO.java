package com.example.dto.notification;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationMessageDTO implements Serializable {
    private Long userId;         // 接收通知的用户
    private Long fromUserId;     // 发起人
    private Integer type;        // 1-6 对应你的枚举
    private Long postId;
    private Long commentId;
    private String content;      // 评论内容
}
