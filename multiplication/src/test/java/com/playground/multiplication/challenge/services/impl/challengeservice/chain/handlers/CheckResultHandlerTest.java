package com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CheckResultHandlerTest {

    private CheckResultHandler checkResultHandler;

    @BeforeEach
    void setUp() {
        checkResultHandler = new CheckResultHandler();
    }

    @Test
    void shouldCheckCorrectResult() {
        //given
        var userId = "userId";
        var factorA = 12;
        var factorB = 23;
        var guess = 276;
        var attempt = new ChallengeAttemptDTO(userId, factorA, factorB, guess);
        var ctx = new AttemptVerifierContext(attempt);

        //when
        checkResultHandler.handle(ctx);

        //then
        var challengeAttempt = ctx.getChallengeAttempt();
        assertAll(
                () -> assertNotNull(challengeAttempt),
                () -> assertEquals(userId, challengeAttempt.getUserId()),
                () -> assertEquals(factorA, challengeAttempt.getFactorA()),
                () -> assertEquals(factorB, challengeAttempt.getFactorB()),
                () -> assertEquals(guess, challengeAttempt.getResultAttempt()),
                () -> assertTrue(challengeAttempt.isCorrect())
        );
    }

    @Test
    void shouldCheckIncorrectResult() {
        //given
        var userId = "userId";
        var factorA = 12;
        var factorB = 23;
        var guess = 2764;
        var attempt = new ChallengeAttemptDTO(userId, factorA, factorB, guess);
        var ctx = new AttemptVerifierContext(attempt);

        //when
        checkResultHandler.handle(ctx);

        //then
        var challengeAttempt = ctx.getChallengeAttempt();
        assertAll(
                () -> assertNotNull(challengeAttempt),
                () -> assertEquals(userId, challengeAttempt.getUserId()),
                () -> assertEquals(factorA, challengeAttempt.getFactorA()),
                () -> assertEquals(factorB, challengeAttempt.getFactorB()),
                () -> assertEquals(guess, challengeAttempt.getResultAttempt()),
                () -> assertFalse(challengeAttempt.isCorrect())
        );
    }
}