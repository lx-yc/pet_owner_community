package com.example.rabbitMQ;

import com.example.dto.notification.NotificationMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.config.RabbitMQConfig.EXCHANGE;
import static com.example.config.RabbitMQConfig.ROUTING_KEY;


@Component
public class NoticeProducer {

    private final RabbitTemplate rabbitTemplate;

    public NoticeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(NotificationMessageDTO dto) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, dto);
    }
}