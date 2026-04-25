package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostLike {
    /** 点赞ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 帖子ID */
    private Long postId;

    /** 点赞时间 */
    private LocalDateTime createTime;
}
