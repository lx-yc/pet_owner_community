package com.example.config;

import com.example.mapper.NotificationMapper;
import com.example.websocket.WebSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public WebSocketServer webSocketServer(RedisTemplate<String, Object> redisTemplate,
                                           NotificationMapper notificationMapper) {
        WebSocketServer.setRedisTemplate(redisTemplate);
        WebSocketServer.setNotificationMapper(notificationMapper);
        return new WebSocketServer();
    }
}
