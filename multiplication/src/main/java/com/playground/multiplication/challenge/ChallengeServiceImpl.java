package com.playground.multiplication.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeAttemptRepository challengeAttemptRepository;

    @Override
    public ChallengeResultDTO verifyAttempt(ChallengeAttemptDTO attempt) {
        var userId = attempt.getUserId();
        var factorA = attempt.getFactorA();
        var factorB = attempt.getFactorB();
        var guess = attempt.getGuess();
        var correctResult = factorA * factorB;

        var isCorrect = factorA * factorB == guess;

        var challengeAttempt = new ChallengeAttempt(null, userId, factorA, factorB, guess, isCorrect);
        saveAttempt(challengeAttempt);
        return new ChallengeResultDTO(userId, factorA, factorB, guess, correctResult, isCorrect);
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
