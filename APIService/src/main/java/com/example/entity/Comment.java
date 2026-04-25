package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    /** 评论ID */
    private Long id;

    /** 帖子ID */
    private Long postId;

    /** 评论用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID（0表示一级评论） */
    private Long parentId;

    /** 被回复用户ID */
    private Long replyUserId;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态：1正常 0删除 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 是否作者评论 */
    private Integer isAuthor;
}
