package com.example.dto.notification;

import lombok.Data;

@Data
public class NoticeDTO {
    private Long fromUserId;  // 谁发的：9
    private Long toUserId;   // 发给谁：5
    private String content;   // 消息内容
}
