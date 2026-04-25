package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentLike {
    /** 点赞ID */
    private Long id;

    /** 点赞用户ID */
    private Long userId;

    /** 评论ID */
    private Long commentId;

    /** 创建时间 */
    private LocalDateTime createTime;
}