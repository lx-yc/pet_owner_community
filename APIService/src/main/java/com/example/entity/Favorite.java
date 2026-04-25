package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    /** 收藏ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 帖子ID */
    private Long postId;

    /** 收藏时间 */
    private LocalDateTime createTime;
}
