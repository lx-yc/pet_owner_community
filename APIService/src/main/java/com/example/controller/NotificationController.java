package com.example.controller;

import com.example.service.NotificationService;
import com.example.utils.GlobalCheckUtil;
import com.example.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知管理", description = "用户消息通知相关接口")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "获取未读消息数量", description = "获取当前用户的未读消息数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Long userId = GlobalCheckUtil.checkLogin();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count, "获取未读消息数量成功");
    }

    @Operation(summary = "清空未读消息", description = "进入通知页面时调用，清空未读消息计数")
    @PostMapping("/unread/clear")
    public Result<Void> clearUnread() {
        Long userId = GlobalCheckUtil.checkLogin();
        notificationService.clearUnread(userId);
        return Result.success("清空未读消息成功");
    }
}
