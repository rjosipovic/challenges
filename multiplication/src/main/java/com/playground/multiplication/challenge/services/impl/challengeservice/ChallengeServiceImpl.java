package com.playground.multiplication.challenge.services.impl.challengeservice;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.api.dto.ChallengeResultDTO;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierChain;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.multiplication.challenge.services.interfaces.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final AttemptVerifierChain attemptVerifierChain;

    @Override
    public ChallengeResultDTO verifyAttempt(ChallengeAttemptDTO attempt) {
        var ctx = new AttemptVerifierContext(attempt);
        attemptVerifierChain.handle(ctx);
        return ctx.getChallengeResult();
    }
}
