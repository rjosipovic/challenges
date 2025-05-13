package com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.challenge_manager.challenge.clients.gamification.dto.ChallengeSolvedDTO;
import com.playground.challenge_manager.challenge.clients.gamification.GamificationClient;
import com.playground.challenge_manager.challenge.messaging.producers.ChallengeSolvedProducer;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublishAttemptHandler implements AttemptHandler {

    private final GamificationClient gamificationClient;
    private final ChallengeSolvedProducer challengeSolvedProducer;

    @Override
    public void handle(AttemptVerifierContext ctx) {
        log.info("Publishing attempt: {}", ctx.getChallengeAttempt());
        var challengeAttempt = ctx.getChallengeAttempt();
        var userId = challengeAttempt.getUserId().toString();
        var attemptId = challengeAttempt.getChallengeAttemptId().toString();
        var firstNumber = challengeAttempt.getFirstNumber();
        var secondNumber = challengeAttempt.getSecondNumber();
        var isCorrect = challengeAttempt.isCorrect();
        var game = challengeAttempt.getGame();
        var challengeSolved = new ChallengeSolvedDTO(userId, attemptId, firstNumber, secondNumber, isCorrect, game);
        try {
            gamificationClient.publishChallengeSolved(challengeSolved);
        } catch (Exception e) {
            log.error("Error publishing attempt: {}", challengeAttempt, e);
        }

        challengeSolvedProducer.publishChallengeSolvedMessage(challengeSolved);
    }
}
