package com.playground.user_manager.user.producers;

import com.playground.user_manager.user.messaging.MessagingConfiguration;
import com.playground.user_manager.user.messaging.producers.UserMessageProducer;
import com.playground.user_manager.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var userId = UUID.randomUUID().toString();
        var alias = "alias";
        var user = new User(userId, alias);

        // when
        userMessageProducer.sendUserCreatedMessage(user);

        // then
        var exchangeCaptor = ArgumentCaptor.forClass(String.class);
        var routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), userCaptor.capture());

        assertAll(
                () -> assertEquals("user.exchange", exchangeCaptor.getValue()),
                () -> assertEquals("user.created", routingKeyCaptor.getValue()),
                () -> assertEquals(user, userCaptor.getValue())
        );
    }
}