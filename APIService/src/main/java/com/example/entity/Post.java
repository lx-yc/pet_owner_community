package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    /** 帖子ID */
    private Long id;

    /** 发布用户ID */
    private Long userId;

    private String title;

    /** 帖子内容（文字） */
    private String content;

    /** 图片列表(JSON数组) */
    private String images;

    /** 类型：normal/宠物分享/心理倾诉 */
    private String type;

    /** 标签（如：焦虑/猫咪/日常） */
    private String tags;

    /** 浏览量 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer favoriteCount;

    /** 评论数 */
    private Integer commentCount;

    /** 状态：1正常 0删除 */
    private Integer status;

    /** 是否置顶 */
    private Integer isTop;

    /** 是否匿名（心理模块用） */
    private Integer isAnonymous;

    /**是否官方文章：0普通帖子 1官方文章 */
    private Integer isOfficial;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 所属季节 */
    private String season;
}
