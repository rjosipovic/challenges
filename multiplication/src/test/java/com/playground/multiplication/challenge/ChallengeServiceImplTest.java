package com.playground.multiplication.challenge;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.dataaccess.entities.ChallengeAttemptEntity;
import com.playground.multiplication.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.multiplication.challenge.services.impl.challengeservice.ChallengeServiceImpl;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierChain;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.AttemptResultHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.CheckResultHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.SaveAttemptHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceImplTest {

    @Mock
    private ChallengeAttemptRepository challengeAttemptRepository;
    private ChallengeServiceImpl challengeService;

    @BeforeEach
    void setUp() {
        var saveAttemptHandler = new SaveAttemptHandler(challengeAttemptRepository);
        var chain = new AttemptVerifierChain();
        chain.addHandler(new CheckResultHandler());
        chain.addHandler(saveAttemptHandler);
        chain.addHandler(new AttemptResultHandler());
        challengeService = new ChallengeServiceImpl(chain);
    }

    @Test
    void testVerifyCorrectAttempt() {
        //given
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = factorA * factorB;
        var attemptDto = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);

        //when
        var result = challengeService.verifyAttempt(attemptDto);

        //then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(factorA, result.getFactorA()),
                () -> assertEquals(factorB, result.getFactorB()),
                () -> assertEquals(guess, result.getGuess()),
                () -> assertEquals(guess, result.getCorrectResult()),
                () -> assertTrue(result.isCorrect())
        );

        verify(challengeAttemptRepository).save(
                new ChallengeAttemptEntity(null, userId, factorA, factorB, guess, true, null)
        );
    }

    @Test
    void testVerifyIncorrectAttempt() {
        var userId = UUID.randomUUID();
        var factorA = 12;
        var factorB = 23;
        var guess = factorA * factorB + 1;
        var correctResult = factorA * factorB;
        var attempt = new ChallengeAttemptDTO(userId.toString(), factorA, factorB, guess);
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
                new ChallengeAttemptEntity(null, userId, factorA, factorB, guess, false, null));
    }
}