package com.playground.multiplication.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

@Value
public class ChallengeAttemptDTO {

    @NotBlank
    @UUID
    String userId;
    @Range(min = 11, max = 99)
    int factorA;
    @Range(min = 11, max = 99)
    int factorB;
    @Positive(message = "Guess must be a positive number")
    int guess;
}
