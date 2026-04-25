package com.example.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    // 点赞类
    POST_LIKE(1, "帖子点赞", "赞了你的帖子"),
    COMMENT_LIKE(2, "评论点赞", "赞了你的评论"),

    // 评论类
    POST_COMMENT(3, "帖子评论", "评论了你的帖子"),
    REPLY_COMMENT(4, "回复评论", "回复了你的评论"),

    // 其他
    FOLLOW(5, "关注", "关注了你"),
    COLLECT(6, "收藏", "收藏了你的帖子");

    // 数据库存储的数字（核心）
    private final Integer code;
    // 内部描述
    private final String desc;
    // 【通知专用】前端展示的文案
    private final String noticeText;

    NotificationType(Integer code, String desc, String noticeText) {
        this.code = code;
        this.desc = desc;
        this.noticeText = noticeText;
    }

    // 根据code获取枚举（通知业务必用）
    public static NotificationType getByCode(Integer code) {
        for (NotificationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    // 【核心方法】自动生成完整通知内容
    // 传入：发起操作的用户ID → 返回：用户xxx 赞了你的帖子
    // ==========================================
    public static String buildContent(Integer code, Long fromUserId) {
        NotificationType type = getByCode(code);
        if (type == null) {
            return "";
        }
        return "用户" + fromUserId + " " + type.getNoticeText();
    }
}
