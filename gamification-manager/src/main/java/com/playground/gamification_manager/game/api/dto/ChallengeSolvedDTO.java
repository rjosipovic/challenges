package com.playground.gamification_manager.game.api.dto;

import com.playground.gamification_manager.game.api.validation.SameDigitCount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

@Value
@SameDigitCount
public class ChallengeSolvedDTO {

    @UUID @NotNull
    String userId;

    @UUID @NotNull
    String challengeAttemptId;

    @NotNull @Range(min = 1, max = 9999)
    Integer firstNumber;

    @NotNull @Range(min = 1, max = 9999)
    Integer secondNumber;

    @NotNull
    boolean correct;

    @NotNull @Pattern(regexp = "addition|subtraction|multiplication|division")
    String game;
}
