package com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.challenge_manager.challenge.dataaccess.entities.ChallengeAttemptEntity;
import com.playground.challenge_manager.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.challenge_manager.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.challenge_manager.challenge.services.model.ChallengeAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveAttemptHandler implements AttemptHandler {

    private final ChallengeAttemptRepository challengeAttemptRepository;

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var challengeAttempt = ctx.getChallengeAttempt();
        var savedAttempt = saveAttempt(challengeAttempt);
        ctx.setChallengeAttempt(savedAttempt);
    }

    private ChallengeAttempt saveAttempt(ChallengeAttempt challengeAttempt) {
        var challengeAttemptEntity = new ChallengeAttemptEntity(
                null,
                challengeAttempt.getUserId(),
                challengeAttempt.getFirstNumber(),
                challengeAttempt.getSecondNumber(),
                challengeAttempt.getResultAttempt(),
                challengeAttempt.isCorrect(),
                challengeAttempt.getGame(),
                null
        );
        var savedAttempt = challengeAttemptRepository.save(challengeAttemptEntity);
        var challengeAttemptId = savedAttempt.getId();
        return challengeAttempt.withChallengeAttemptId(challengeAttemptId);
    }
}
