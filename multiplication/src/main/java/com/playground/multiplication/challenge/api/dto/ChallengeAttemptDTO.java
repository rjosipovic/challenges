package com.playground.multiplication.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Value
public class ChallengeAttemptDTO {

    @NotBlank
    String userId;
    @Range(min = 11, max = 99)
    int factorA;
    @Range(min = 11, max = 99)
    int factorB;
    @Positive(message = "Guess must be a positive number")
    int guess;
}
