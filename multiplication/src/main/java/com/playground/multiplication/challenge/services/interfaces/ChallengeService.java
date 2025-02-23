package com.playground.multiplication.challenge.services.interfaces;

import com.playground.multiplication.challenge.api.dto.ChallengeAttemptDTO;
import com.playground.multiplication.challenge.api.dto.ChallengeResultDTO;

public interface ChallengeService {

    ChallengeResultDTO verifyAttempt(ChallengeAttemptDTO attempt);
}
