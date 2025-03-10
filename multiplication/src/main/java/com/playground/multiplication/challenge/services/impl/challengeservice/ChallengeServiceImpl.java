package com.playground.multiplication.challenge.services.impl.challengeservice;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.api.dto.ChallengeResultDTO;
import com.playground.multiplication.challenge.dataaccess.repositories.ChallengeAttemptRepository;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierChain;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierContext;
import com.playground.multiplication.challenge.services.interfaces.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final AttemptVerifierChain attemptVerifierChain;
    private final ChallengeAttemptRepository challengeAttemptRepository;

    @Override
    public ChallengeResultDTO verifyAttempt(ChallengeAttemptDTO attempt) {
        var ctx = new AttemptVerifierContext(attempt);
        attemptVerifierChain.handle(ctx);
        return ctx.getChallengeResult();
    }

    @Override
    public List<ChallengeResultDTO> findLast10ResultsForUser(UUID userId) {
        return challengeAttemptRepository.findLast10AttemptsByUser(userId)
                .stream()
                .map(entity -> new ChallengeResultDTO(
                        entity.getUserId().toString(),
                        entity.getFactorA(),
                        entity.getFactorB(),
                        entity.getResultAttempt(),
                        entity.getFactorA() * entity.getFactorB(),
                        entity.isCorrect()))
                .toList();
    }
}
