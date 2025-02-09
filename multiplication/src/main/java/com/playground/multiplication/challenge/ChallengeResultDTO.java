package com.playground.multiplication.challenge;

import lombok.Value;

@Value
public class ChallengeResultDTO {

    String userAlias;
    int factorA;
    int factorB;
    int guess;
    int correctResult;
    boolean correct;
}
