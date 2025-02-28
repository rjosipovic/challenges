package com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers;


import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.multiplication.challenge.services.model.ChallengeAttempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AttemptResultHandlerTest {

    private AttemptResultHandler attemptResultHandler;

    @BeforeEach
    void setUp() {
        attemptResultHandler = new AttemptResultHandler();
    }

    @Test
    void shouldCreateCorrectAttemptResult() {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = 276;
        var isCorrect = true;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);
        var challengeAttempt = new ChallengeAttempt(null, userId, factorA, factorB, guess, isCorrect);
        var ctx = new AttemptVerifierContext(attempt);
        ctx.setChallengeAttempt(challengeAttempt);

        //when
        attemptResultHandler.handle(ctx);

        //then
        var result = ctx.getChallengeResult();
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userId.toString(), result.getUserId()),
                () -> assertEquals(factorA, result.getFactorA()),
                () -> assertEquals(factorB, result.getFactorB()),
                () -> assertEquals(guess, result.getGuess()),
                () -> assertEquals(factorA * factorB, result.getCorrectResult()),
                () -> assertTrue(result.isCorrect())
        );
    }

    @Test
    void shouldCreateIncorrectAttemptResult() {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = 456;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);
        var challengeAttempt = new ChallengeAttempt(null, userId, factorA, factorB, guess, false);
        var ctx = new AttemptVerifierContext(attempt);
        ctx.setChallengeAttempt(challengeAttempt);

        //when
        attemptResultHandler.handle(ctx);

        //then
        var result = ctx.getChallengeResult();
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userId.toString(), result.getUserId()),
                () -> assertEquals(factorA, result.getFactorA()),
                () -> assertEquals(factorB, result.getFactorB()),
                () -> assertEquals(guess, result.getGuess()),
                () -> assertEquals(factorA * factorB, result.getCorrectResult()),
                () -> assertFalse(result.isCorrect())
        );
    }
}