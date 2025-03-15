package com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.challenge_manager.challenge.api.dto.ChallengeResultDTO;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.util.MathUtil;

public class AttemptResultHandler implements AttemptHandler {

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var challengeAttempt = ctx.getChallengeAttempt();
        var userId = challengeAttempt.getUserId().toString();
        var firstNumber = challengeAttempt.getFirstNumber();
        var secondNumber = challengeAttempt.getSecondNumber();
        var guess = challengeAttempt.getResultAttempt();
        var isCorrect = challengeAttempt.isCorrect();
        var game = challengeAttempt.getGame();
        var correctResult = MathUtil.calculateResult(firstNumber, secondNumber, game);

        var result = new ChallengeResultDTO(userId, firstNumber, secondNumber, guess, correctResult, isCorrect, game);
        ctx.setChallengeResult(result);
    }
}
