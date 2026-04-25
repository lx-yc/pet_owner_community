package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Follow {
    /** 主键ID */
    private Long id;

    /** 用户ID（谁发起关注） */
    private Long userId;

    /** 被关注用户ID */
    private Long followUserId;

    /** 关注时间 */
    private LocalDateTime createTime;
}