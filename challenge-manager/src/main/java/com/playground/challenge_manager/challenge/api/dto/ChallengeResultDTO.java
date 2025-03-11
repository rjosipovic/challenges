package com.playground.challenge_manager.challenge.api.dto;

import lombok.Value;

@Value
public class ChallengeResultDTO {

    String userId;
    int factorA;
    int factorB;
    int guess;
    int correctResult;
    boolean correct;
}
