package com.example.vo.Comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO implements CursorIdAware{
    private Long id;
    private Long postId;
    private Long userId;      // 评论人ID
    private String content;   // 内容
    private Long parentId;    // 0
    private Long replyUserId; // 被回复人ID（一级评论一般为null）

    private String nickname;  // 评论人昵称（关联user表）
    private String avatar;    // 评论人头像

    private Integer likeCount;
    private Integer isAuthor; // 是否楼主
    private LocalDateTime createTime;

    private Boolean isLiked;

    private Integer replyCount;

    @Override
    public Long getId() {
        return id;
    }
}
