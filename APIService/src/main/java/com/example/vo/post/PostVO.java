package com.example.vo.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    private Long id;                // 帖子ID
    private Long userId;            // 用户ID
    private String title;           //
    private String content;         // 内容
    private String images;          // 图片
    private String type;            // 类型
    private String tags;            // 标签
    private Integer viewCount;      // 浏览量
    private Integer likeCount;      // 点赞数
    private Integer favoriteCount;  // 收藏数
    private Integer commentCount;   // 评论数
    private Integer status;         // 状态
    private Integer isTop;          // 是否置顶
    private Integer isAnonymous;    // 是否匿名
    private Integer isOfficial;     // 是否官方
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime;

    private String season;

    private String userNickname;
    private String userAvatar;

    private Boolean isLiked;   // 是否已点赞
    private Boolean isCollected; // 是否已收藏
}
