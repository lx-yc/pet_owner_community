package com.example.vo.Comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyVO implements CursorIdAware{
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Long parentId;
    private Long replyUserId;    // 被回复用户ID

    private String nickname;     // 我的昵称
    private String avatar;

    private String replyNickname; // 被回复人的昵称
    private String replyAvatar;   // 被回复人的头像

    private Integer likeCount;
    private LocalDateTime createTime;

    private Boolean isLiked;

    @Override
    public Long getId() {
        return id;
    }
}
