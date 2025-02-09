package com.playground.multiplication.challenge;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Value
public class ChallengeAttemptDTO {

    @NotEmpty
    String userAlias;
    @Range(min = 11, max = 99)
    int factorA;
    @Range(min = 11, max = 99)
    int factorB;
    @Positive(message = "Guess must be a positive number")
    int guess;
}
