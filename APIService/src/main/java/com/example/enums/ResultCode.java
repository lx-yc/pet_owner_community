package com.example.enums;


import lombok.Getter;

@Getter
public enum ResultCode {
    // 通用
    SUCCESS(200, "成功"),
    FAILED(401, "请先登录"),
    ERROR(500, "服务器异常"),

    // 用户 600~699
    USER_NOT_EXIST(601, "用户不存在"),
    PASSWORD_ERROR(602, "密码错误"),
    USER_DISABLED(603, "用户被禁用"),
    USER_EXIST(604, "用户已存在"),
    USER_NO_PERMISSION(605, "无管理员权限"),
    REGISTER_FAILED(606, "注册失败"),
    USER_UPDATE_FAILED(607, "用户信息更新失败"),
    USER_FOLLOW_CANCEL_FAILED(608, "取消关注失败"),
    USER_FOLLOW_ADD_FAILED(609, "关注失败"),
    USER_FOLLOW_SELF(610, "不能关注自己"),

    // 宠物 700~799
    PET_NOT_EXIST(701, "宠物不存在"),
    PET_NO_PERMISSION(702, "无权限操作该宠物"),
    PET_INSERT_FAILED(703, "新增宠物失败"),
    PET_UPDATE_FAILED(704, "更新宠物失败"),
    PET_DELETE_FAILED(705, "删除宠物失败"),

    // 帖子 800~899
    POST_NOT_EXIST(801, "帖子不存在"),
    POST_INSERT_FAILED(802, "新增帖子失败"),
    POST_NO_PERMISSION(803, "无权限操作该帖子"),
    POST_UPDATE_FAILED(804, "更新帖子失败"),
    POST_DELETE_FAILED(805, "删除帖子失败"),
    POST_LIKE_CANCEL_FAILED(806, "取消点赞失败"),
    POST_LIKE_ADD_FAILED(807, "点赞失败"),
    POST_FAVORITE_CANCEL_FAILED(808, "取消收藏失败"),
    POST_FAVORITE_ADD_FAILED(809, "收藏失败"),
    POST_VIEW_COUNT_INCREASE_FAILED(810, "浏览量增加失败"),
    POST_COMMENT_COUNT_INCREASE_FAILED(811, "评论数增加失败"),
    POST_COMMENT_COUNT_DECREASE_FAILED(812, "评论数减少失败"),

    //评论
    COMMENT_NOT_EXIST(901, "评论不存在"),
    COMMENT_INSERT_FAILED(902, "新增评论失败"),
    COMMENT_NO_PERMISSION(903, "无权限操作该评论"),
    COMMENT_UPDATE_FAILED(904, "更新评论失败"),
    COMMENT_DELETE_FAILED(905, "删除评论失败"),
    COMMENT_LIKE_ADD_FAILED(906, "点赞失败"),
    COMMENT_LIKE_CANCEL_FAILED(907, "取消点赞失败"),

    // 通知
    NOTICE_SEND_FAILED(1001, "发送通知失败"),
    NOTIFICATION_CLEAR_FAILED(1002, "清空通知失败");


    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
