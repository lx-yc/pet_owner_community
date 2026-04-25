package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    /** 通知ID */
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 触发用户ID */
    private Long fromUserId;

    /** 类型：like/comment/follow */
    private Integer type;

    /** 关联帖子ID */
    private Long postId;

    /** 关联评论ID */
    private Long commentId;

    /** 通知内容 */
    private String content;

    /** 是否已读：0未读 1已读 */
    private Integer isRead;

    /** 创建时间 */
    private LocalDateTime createTime;
}