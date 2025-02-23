package com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.multiplication.challenge.api.dto.ChallengeResultDTO;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;

public class AttemptResultHandler implements AttemptHandler {

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var challengeAttempt = ctx.getChallengeAttempt();
        var userId = challengeAttempt.getUserId();
        var factorA = challengeAttempt.getFactorA();
        var factorB = challengeAttempt.getFactorB();
        var guess = challengeAttempt.getResultAttempt();
        var isCorrect = challengeAttempt.isCorrect();
        var correctResult = factorA * factorB;

        var result = new ChallengeResultDTO(userId, factorA, factorB, guess, correctResult, isCorrect);
        ctx.setChallengeResult(result);
    }
}
