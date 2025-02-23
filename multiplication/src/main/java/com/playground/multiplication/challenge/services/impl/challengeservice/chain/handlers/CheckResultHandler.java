package com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers;


import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.multiplication.challenge.services.model.ChallengeAttempt;

public class CheckResultHandler implements AttemptHandler {

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var attempt = ctx.getAttempt();
        var userId = attempt.getUserId();
        var factorA = attempt.getFactorA();
        var factorB = attempt.getFactorB();
        var guess = attempt.getGuess();

        var isCorrect = factorA * factorB == guess;

        var challengeAttempt = new ChallengeAttempt(null, userId, factorA, factorB, guess, isCorrect);
        ctx.setChallengeAttempt(challengeAttempt);
    }
}
