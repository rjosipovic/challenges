package com.playground.user_manager.user.producers;

import com.playground.user_manager.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MessagingConfiguration messagingConfiguration;

    @Mock
    private MessagingConfiguration.UserMessageConfiguration userMessageConfiguration;

    @InjectMocks
    private UserMessageProducer userMessageProducer;

    @BeforeEach
    void setUp() {
        when(messagingConfiguration.getUser()).thenReturn(userMessageConfiguration);
        when(userMessageConfiguration.getExchange()).thenReturn("user.exchange");
        when(userMessageConfiguration.getUserCreatedRoutingKey()).thenReturn("user.created");
    }

    @Test
    void shouldSendUserCreatedMessage() {
        // given
        var user = new User("1", "test-alias");

        // when
        userMessageProducer.sendUserCreatedMessage(user);

        // then
        verify(rabbitTemplate).convertAndSend("user.exchange", "user.created", user);
    }
}