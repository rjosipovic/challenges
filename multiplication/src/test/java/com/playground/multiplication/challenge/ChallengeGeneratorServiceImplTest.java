package com.playground.multiplication.challenge;

import com.playground.multiplication.challenge.services.model.Challenge;
import com.playground.multiplication.challenge.services.impl.ChallengeGeneratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeGeneratorServiceImplTest {

    private static final int MIN = 11;
    private static final int MAX = 99;

    @Spy
    private Random random;

    private ChallengeGeneratorServiceImpl challengeGeneratorService;

    @BeforeEach
    void setUp() {
        challengeGeneratorService = new ChallengeGeneratorServiceImpl(random, MIN, MAX);
    }

    @Test
    void testGenerateChallenge() {

        when(random.nextInt((MAX - MIN) + 1)).thenReturn(11, 12);
        var challenge = challengeGeneratorService.randomChallenge();
        assertNotNull(challenge);
        assertTrue(challenge.getFactorA() >= MIN && challenge.getFactorA() <= MAX);
        assertTrue(challenge.getFactorB() >= MIN && challenge.getFactorB() <= MAX);
        assertEquals(challenge, new Challenge(11 + 11, 12 + 11));
    }
}