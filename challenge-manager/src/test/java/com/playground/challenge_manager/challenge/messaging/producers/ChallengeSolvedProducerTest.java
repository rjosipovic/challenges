package com.playground.challenge_manager.challenge.messaging.producers;

import com.playground.challenge_manager.challenge.clients.gamification.dto.ChallengeSolvedDTO;
import com.playground.challenge_manager.challenge.messaging.MessagingConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChallengeSolvedProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MessagingConfiguration messagingConfiguration;

    @Mock
    private MessagingConfiguration.ChallengeConfiguration challengeConfiguration;

    @InjectMocks
    private ChallengeSolvedProducer producer;

    @BeforeEach
    void setUp() {
        when(messagingConfiguration.getChallenge()).thenReturn(challengeConfiguration);
        when(challengeConfiguration.getExchange()).thenReturn("challenge-exchange");
        when(challengeConfiguration.getChallengeCorrectRoutingKey()).thenReturn("challenge.correct");
        when(challengeConfiguration.getChallengeFailedRoutingKey()).thenReturn("challenge.failed");
    }

    @Test
    void shouldSendToCorrectRoutingKeyWhenCorrect() {
        // given
        var dto = mock(ChallengeSolvedDTO.class);
        when(dto.isCorrect()).thenReturn(true);

        // when
        producer.publishChallengeSolvedMessage(dto);

        // then
        verify(rabbitTemplate).convertAndSend("challenge-exchange", "challenge.correct", dto);
        verify(rabbitTemplate, never()).convertAndSend("challenge-exchange", "challenge.failed", dto);
    }

    @Test
    void shouldSendToFailedRoutingKeyWhenIncorrect() {
        // given
        var dto = mock(ChallengeSolvedDTO.class);
        when(dto.isCorrect()).thenReturn(false);

        // when
        producer.publishChallengeSolvedMessage(dto);

        // then
        verify(rabbitTemplate).convertAndSend("challenge-exchange", "challenge.failed", dto);
        verify(rabbitTemplate, never()).convertAndSend("challenge-exchange", "challenge.correct", dto);
    }
}