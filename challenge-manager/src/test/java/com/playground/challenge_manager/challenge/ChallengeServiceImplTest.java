package com.playground.challenge_manager.challenge;

import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.dataaccess.entities.ChallengeAttemptEntity;
import com.playground.challenge_manager.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.ChallengeServiceImpl;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierChain;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers.AttemptResultHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers.CheckResultHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers.SaveAttemptHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        challengeService = new ChallengeServiceImpl(chain, challengeAttemptRepository);
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

    @Test
    void testGetLast10AttemptsByUserId_shouldReturnEmptyList() {
        //given
        var userId = UUID.randomUUID();
        when(challengeAttemptRepository.findLast10AttemptsByUser(userId)).thenReturn(List.of());
        //when
        var result = challengeService.findLast10ResultsForUser(userId);
        //then
        verify(challengeAttemptRepository).findLast10AttemptsByUser(userId);
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void testGetLast10AttemptsByUserId_shouldReturnNonEmptyList() {
        //given
        var userId = UUID.randomUUID();
        var attemptId = UUID.randomUUID();
        var attempt = new ChallengeAttemptEntity(attemptId, userId, 1, 2, 3, true, ZonedDateTime.now().minusDays(1));
        when(challengeAttemptRepository.findLast10AttemptsByUser(userId)).thenReturn(List.of(attempt));
        //when
        var result = challengeService.findLast10ResultsForUser(userId);
        //then
        verify(challengeAttemptRepository).findLast10AttemptsByUser(userId);
        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(result.isEmpty()),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(userId.toString(), result.get(0).getUserId()),
                () -> assertEquals(1, result.get(0).getFactorA()),
                () -> assertEquals(2, result.get(0).getFactorB()),
                () -> assertEquals(3, result.get(0).getGuess()),
                () -> assertTrue(result.get(0).isCorrect())
        );
    }
}