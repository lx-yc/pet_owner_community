package com.example.service.impl;

import com.example.entity.Notification;
import com.example.enums.ResultCode;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import com.example.utils.GlobalCheckUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private NotificationMapper notificationMapper;

    // Redis KEY 规范
    private static final String UNREAD_PREFIX = "notice:unread:user:";

    /**
     * 获取未读消息数
     */
    @Override
    public int getUnreadCount(Long userId) {
        Object count = redisTemplate.opsForValue().get(UNREAD_PREFIX + userId);
        return count == null ? 0 : (Integer) count;
    }

    /**
     * 清空未读（用户查看通知时调用）
     */
    @Override
    public void clearUnread(Long userId) {
        redisTemplate.delete(UNREAD_PREFIX + userId);
        int rows = notificationMapper.updateRead(userId);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.NOTIFICATION_CLEAR_FAILED);
        // 数据库标记已读
        notificationMapper.updateRead(userId);

        // 清空 Redis 未读计数
        redisTemplate.delete(UNREAD_PREFIX + userId);
    }

}
