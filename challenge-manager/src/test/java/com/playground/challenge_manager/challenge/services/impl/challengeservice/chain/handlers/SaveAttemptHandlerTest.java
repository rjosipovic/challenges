package com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.challenge_manager.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.challenge_manager.challenge.dataaccess.entities.ChallengeAttemptEntity;
import com.playground.challenge_manager.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.challenge_manager.challenge.services.model.ChallengeAttempt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveAttemptHandlerTest {

    @Mock
    private ChallengeAttemptRepository challengeAttemptRepository;

    @InjectMocks
    private SaveAttemptHandler saveAttemptHandler;

    @Test
    void shouldSaveAttempt() {
        //given
        var userId = UUID.randomUUID();
        var challengeAttemptId = UUID.randomUUID();
        var firstNumber = 12;
        var secondNumber = 23;
        var guess = 2764;
        var game = "multiplication";
        var difficulty = "easy";
        var attempt = new ChallengeAttemptDTO(userId.toString(), firstNumber, secondNumber, guess, game);
        var ctx = new AttemptVerifierContext(attempt);
        var challengeAttempt = new ChallengeAttempt(null, userId, firstNumber, secondNumber, guess, false, game, difficulty, null);
        ctx.setChallengeAttempt(challengeAttempt);

        var entity = new ChallengeAttemptEntity(null, userId, firstNumber, secondNumber, guess, false, game, difficulty, null);
        var saved = new ChallengeAttemptEntity(challengeAttemptId, userId, firstNumber, secondNumber, guess, false, game, difficulty, null);
        when(challengeAttemptRepository.saveAndFlush(entity)).thenReturn(saved);

        //when
        saveAttemptHandler.handle(ctx);
        //then
        verify(challengeAttemptRepository, times(1)).saveAndFlush(entity);
    }
}