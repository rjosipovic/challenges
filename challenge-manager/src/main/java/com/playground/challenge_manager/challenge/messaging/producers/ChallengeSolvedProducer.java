package com.playground.challenge_manager.challenge.messaging.producers;

import com.playground.challenge_manager.challenge.messaging.events.ChallengeSolvedEvent;
import com.playground.challenge_manager.challenge.messaging.MessagingConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeSolvedProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingConfiguration messagingConfiguration;

    public void publishChallengeSolvedMessage(ChallengeSolvedEvent challengeSolvedDTO) {
        var challengeConfiguration = messagingConfiguration.getChallenge();
        var exchange = challengeConfiguration.getExchange();
        var correctRoutingKey = challengeConfiguration.getChallengeCorrectRoutingKey();
        var incorrectRoutingKey = challengeConfiguration.getChallengeFailedRoutingKey();

        var isCorrect = challengeSolvedDTO.isCorrect();

        if (isCorrect) {
            sendChallengeSolvedMessage(exchange, correctRoutingKey, challengeSolvedDTO);
        } else {
            sendChallengeSolvedMessage(exchange, incorrectRoutingKey, challengeSolvedDTO);
        }
    }

    private void sendChallengeSolvedMessage(String exchange, String routingKey, ChallengeSolvedEvent challengeSolvedDTO) {
        rabbitTemplate.convertAndSend(exchange, routingKey, challengeSolvedDTO);
    }
}
