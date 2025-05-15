package com.playground.challenge_manager.challenge.messaging.producers;

import com.playground.challenge_manager.challenge.messaging.events.ChallengeSolvedEvent;
import com.playground.challenge_manager.challenge.messaging.MessagingConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var dto = mock(ChallengeSolvedEvent.class);
        when(dto.isCorrect()).thenReturn(true);

        // when
        producer.publishChallengeSolvedMessage(dto);

        // then
        var exchangeCaptor = ArgumentCaptor.forClass(String.class);
        var routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        var challengeSolvedEventCaptor = ArgumentCaptor.forClass(ChallengeSolvedEvent.class);
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), challengeSolvedEventCaptor.capture());

        assertAll(
                () -> assertEquals("challenge-exchange", exchangeCaptor.getValue()),
                () -> assertEquals("challenge.correct", routingKeyCaptor.getValue()),
                () -> assertEquals(dto, challengeSolvedEventCaptor.getValue())
        );
        verify(rabbitTemplate, never()).convertAndSend("challenge-exchange", "challenge.failed", dto);
    }

    @Test
    void shouldSendToFailedRoutingKeyWhenIncorrect() {
        // given
        var dto = mock(ChallengeSolvedEvent.class);
        when(dto.isCorrect()).thenReturn(false);

        // when
        producer.publishChallengeSolvedMessage(dto);

        // then
        var exchangeCaptor = ArgumentCaptor.forClass(String.class);
        var routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        var challengeSolvedEventCaptor = ArgumentCaptor.forClass(ChallengeSolvedEvent.class);
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), challengeSolvedEventCaptor.capture());

        assertAll(
                () -> assertEquals("challenge-exchange", exchangeCaptor.getValue()),
                () -> assertEquals("challenge.failed", routingKeyCaptor.getValue()),
                () -> assertEquals(dto, challengeSolvedEventCaptor.getValue())
        );
        verify(rabbitTemplate, never()).convertAndSend("challenge-exchange", "challenge.correct", dto);
    }
}