package com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers;


import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.util.MathUtil;
import com.playground.challenge_manager.challenge.services.model.ChallengeAttempt;

import java.util.UUID;

public class CheckResultHandler implements AttemptHandler {

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var attempt = ctx.getAttempt();
        var userId = UUID.fromString(attempt.getUserId());
        var firstNumber = attempt.getFirstNumber();
        var secondNumber = attempt.getSecondNumber();
        var guess = attempt.getGuess();
        var game = attempt.getGame();

        var isCorrect = isCorrect(guess, MathUtil.calculateResult(firstNumber, secondNumber, game));

        var challengeAttempt = new ChallengeAttempt(userId, firstNumber, secondNumber, guess, isCorrect, game);
        ctx.setChallengeAttempt(challengeAttempt);
    }

    private boolean isCorrect(int guess, int correctResult) {
        return guess == correctResult;
    }
}
