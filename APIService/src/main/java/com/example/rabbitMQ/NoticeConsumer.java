package com.example.rabbitMQ;

import com.example.dto.notification.NotificationMessageDTO;
import com.example.entity.Notification;
import com.example.enums.ResultCode;
import com.example.mapper.NotificationMapper;
import com.example.utils.BeanConvertUtils;
import com.example.utils.GlobalCheckUtil;
import com.example.websocket.WebSocketServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.config.RabbitMQConfig.QUEUE;

@Component
@RequiredArgsConstructor
public class NoticeConsumer {

    @Resource
    private NotificationMapper notificationMapper;

    // 注入 Redis
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Redis KEY 规范
    private static final String UNREAD_PREFIX = "notice:unread:user:";
    private static final String WS_ONLINE_KEY = "user:online:set";

    @RabbitListener(queues = QUEUE)
    public void receive(NotificationMessageDTO dto) {
        doDatabaseBusiness(dto);
        pushWebSocket(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doDatabaseBusiness(NotificationMessageDTO dto){
        // 1. 写入数据库通知表
        Notification notice = BeanConvertUtils.convert(dto, Notification.class);
        notice.setIsRead(0); // 未读

        int rows = notificationMapper.insert(notice);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.NOTICE_SEND_FAILED);

        //redis
        redisTemplate.opsForValue().increment(UNREAD_PREFIX + dto.getUserId(),1);
    }

    public void pushWebSocket(NotificationMessageDTO dto){
        Long userId = dto.getUserId();
        String userIdStr = userId.toString();
        try {
            // 1. 检查在线状态
            Boolean isOnline = redisTemplate.opsForSet().isMember(WS_ONLINE_KEY, userIdStr);
            Session session = WebSocketServer.SESSION_POOL.get(userId);

            // 2. 只有在线且 Session 有效才推送
            if (Boolean.TRUE.equals(isOnline) && session != null && session.isOpen()) {

                // 【关键修改】构造 JSON 对象
                Map<String, Object> message = new HashMap<>();
                message.put("type", "notification");
                message.put("id", System.currentTimeMillis()); // 临时ID，或者使用 dto.getId() 如果有
                message.put("content", dto.getContent());
                message.put("createTime", LocalDateTime.now().toString());
                message.put("isRead", false);

                String jsonMsg = objectMapper.writeValueAsString(message);

                // 发送 JSON 字符串
                session.getAsyncRemote().sendText(jsonMsg);

//                System.out.println("【DEBUG】已向用户" + userId + " 推送 JSON 消息: " + jsonMsg);
            } else {
//                System.out.println("【DEBUG】用户" + userId + " 不在线或 Session 无效，跳过推送");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}