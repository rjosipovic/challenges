package com.playground.multiplication.challenge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceImplTest {

    @Mock
    private ChallengeAttemptRepository challengeAttemptRepository;

    @InjectMocks
    private ChallengeServiceImpl challengeService;

    @Test
    void testVerifyCorrectAttempt() {
        var userAlias = "alias";
        var factorA = 12;
        var factorB = 23;
        var guess = factorA * factorB;
        var attemptDto = new ChallengeAttemptDTO(userAlias, factorA, factorB, guess);
        var result = challengeService.verifyAttempt(attemptDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(factorA, result.getFactorA()),
                () -> assertEquals(factorB, result.getFactorB()),
                () -> assertEquals(guess, result.getGuess()),
                () -> assertEquals(guess, result.getCorrectResult()),
                () -> assertTrue(result.isCorrect())
        );

        verify(challengeAttemptRepository).save(
                new ChallengeAttemptEntity(null, null, factorA, factorB, guess, true, null)
        );
    }

    @Test
    void testVerifyIncorrectAttempt() {
        var userAlias = "alias";
        var factorA = 12;
        var factorB = 23;
        var guess = factorA * factorB + 1;
        var correctResult = factorA * factorB;
        var attempt = new ChallengeAttemptDTO(userAlias, factorA, factorB, guess);
        var result = challengeService.verifyAttempt(attempt);

        assertAll(
                () -> assertNotNull(attempt),
                () -> assertEquals(factorA, attempt.getFactorA()),
                () -> assertEquals(factorB, attempt.getFactorB()),
                () -> assertEquals(guess, attempt.getGuess()),
                () -> assertEquals(correctResult, result.getCorrectResult()),
                () -> assertFalse(result.isCorrect())
        );

        verify(challengeAttemptRepository).save(
                new ChallengeAttemptEntity(null, null, factorA, factorB, guess, false, null));
    }
}