package com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers;

import com.playground.multiplication.challenge.dataaccess.entities.ChallengeAttemptEntity;
import com.playground.multiplication.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.multiplication.challenge.services.model.ChallengeAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveAttemptHandler implements AttemptHandler {

    private final ChallengeAttemptRepository challengeAttemptRepository;

    @Override
    public void handle(AttemptVerifierContext ctx) {
        var challengeAttempt = ctx.getChallengeAttempt();
        saveAttempt(challengeAttempt);
    }

    private void saveAttempt(ChallengeAttempt challengeAttempt) {
        var challengeAttemptEntity = new ChallengeAttemptEntity(
                null,
                challengeAttempt.getUserId(),
                challengeAttempt.getFactorA(),
                challengeAttempt.getFactorB(),
                challengeAttempt.getResultAttempt(),
                challengeAttempt.isCorrect(),
                null
        );
        challengeAttemptRepository.save(challengeAttemptEntity);
    }
}
