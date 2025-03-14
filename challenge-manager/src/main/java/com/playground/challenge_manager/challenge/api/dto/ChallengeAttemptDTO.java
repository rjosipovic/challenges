package com.playground.challenge_manager.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

@Value
public class ChallengeAttemptDTO {

    @NotBlank
    @UUID
    String userId;
    @NotNull
    @Range(min = 1, max = 9999)
    Integer firstNumber;
    @NotNull
    @Range(min = 1, max = 9999)
    Integer secondNumber;
    @NotNull
    Integer guess;
    @NotNull @Pattern(regexp = "addition|subtraction|multiplication|division")
    String game;
}
