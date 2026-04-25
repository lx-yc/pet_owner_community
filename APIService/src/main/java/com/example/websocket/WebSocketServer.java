package com.example.websocket;

import com.example.entity.Notification;
import com.example.enums.ResultCode;
import com.example.mapper.NotificationMapper;
import com.example.utils.GlobalCheckUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static NotificationMapper notificationMapper;

    private static RedisTemplate<String, Object> redisTemplate;
    // 注入
    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        WebSocketServer.redisTemplate = redisTemplate;
    }

    public static void setNotificationMapper(NotificationMapper notificationMapper) {
        WebSocketServer.notificationMapper = notificationMapper;
    }

    // 在线会话：userId -> Session
    public static final Map<Long, Session> SESSION_POOL = new ConcurrentHashMap<>();
    private static final String ONLINE_SET_KEY = "user:online:set";
    private static final String UNREAD_PREFIX = "notice:unread:user:";


    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        SESSION_POOL.put(userId, session);
        redisTemplate.opsForSet().add(ONLINE_SET_KEY, userId.toString());
        log.info("用户{} 连接WebSocket", userId);
        pushUnreadMessages(userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSION_POOL.remove(userId);
        redisTemplate.opsForSet().remove(ONLINE_SET_KEY, userId.toString());
        log.info("用户{} 断开WebSocket", userId);
    }

    @OnError
    public void onError(Throwable error) {
        log.error("WebSocket异常", error);
    }

    // 心跳检测：客户端每10秒发一次ping，服务端响应pong
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") Long userId) {
        if ("PING".equals(message)) { // 注意大小写！
            // 关键：收到 PING，刷新 Redis 中的在线状态
            redisTemplate.opsForSet().add(ONLINE_SET_KEY, userId.toString());
            log.info("收到用户{}的心跳，刷新Redis在线状态", userId);
            try {
                session.getBasicRemote().sendText("PONG");
            } catch (Exception e) {
                log.error("心跳响应失败", e);
            }
        }
    }

    // ====================== 【自动推未读消息】核心方法 ======================

    private void pushUnreadMessages(Long userId) {
        try {
            List<Notification> unreadList = notificationMapper.selectByUserId(userId);
            if (unreadList.isEmpty()) {
                log.info("用户{} 暂无未读消息", userId);
                return;
            }

            log.info("用户{} 共有{}条未读消息，开始推送", userId, unreadList.size());

            Session session = SESSION_POOL.get(userId);
            if (session != null && session.isOpen()) {
                for (Notification notice : unreadList) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("type", "notification");
                    message.put("content", notice.getContent());
                    message.put("createTime", notice.getCreateTime().toString());
                    message.put("id", notice.getId());
                    message.put("isRead", false);

                    String jsonMsg = objectMapper.writeValueAsString(message);
                    session.getBasicRemote().sendText(jsonMsg);
                }
            }

            // 数据库标记已读
//            notificationMapper.updateRead(userId);

            // 清空 Redis 未读计数
//            redisTemplate.delete(UNREAD_PREFIX + userId);

            log.info("用户{} 未读消息推送完成，已全部标记已读", userId);

        } catch (Exception e) {
            log.error("推送未读消息失败", e);
        }
    }

    // 发送消息给指定用户
    public static void sendToUser(Long userId, String content) {
        Session session = SESSION_POOL.get(userId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> message = new HashMap<>();
                message.put("type", "notification");
                message.put("content", content);
                message.put("createTime", LocalDateTime.now().toString());
                message.put("id", System.currentTimeMillis()); // 临时 ID
                message.put("isRead", false);

                String jsonMsg = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(jsonMsg);
                log.info("WebSocket 推送成功 → 用户{}：{}", userId, content);
            } catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }


}
