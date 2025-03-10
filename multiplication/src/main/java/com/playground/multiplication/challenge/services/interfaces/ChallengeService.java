package com.playground.multiplication.challenge.services.interfaces;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.api.dto.ChallengeResultDTO;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {

    ChallengeResultDTO verifyAttempt(ChallengeAttemptDTO attempt);
    List<ChallengeResultDTO> findLast10ResultsForUser(UUID userId);
}
