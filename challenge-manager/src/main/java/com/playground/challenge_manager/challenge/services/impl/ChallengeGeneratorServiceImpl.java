package com.playground.challenge_manager.challenge.services.impl;

import com.playground.challenge_manager.challenge.services.interfaces.ChallengeGeneratorService;
import com.playground.challenge_manager.challenge.services.model.Challenge;

import java.util.Random;

public class ChallengeGeneratorServiceImpl implements ChallengeGeneratorService {

    private final Random random;
    private final int min;
    private final int max;

    public ChallengeGeneratorServiceImpl(final Random random, final int min, final int max) {
        this.min = min;
        this.max = max;
        this.random = random;
    }

    @Override
    public Challenge randomChallenge() {
        var factorA = next();
        var factorB = next();
        return new Challenge(factorA, factorB);
    }

    private int next() {
        return random.nextInt((max - min) + 1) + min;
    }
}
