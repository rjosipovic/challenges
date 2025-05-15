package com.playground.user_manager.user.messaging.producers;

import com.playground.user_manager.user.messaging.MessagingConfiguration;
import com.playground.user_manager.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingConfiguration messagingConfiguration;

    public void sendUserCreatedMessage(User user) {
        log.info("About to publish user created message: {}", user);
        var userMessagingConfiguration = messagingConfiguration.getUser();
        var exchange = userMessagingConfiguration.getExchange();
        var routingKey = userMessagingConfiguration.getUserCreatedRoutingKey();
        sendMessage(exchange, routingKey, user);
    }

    private void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
