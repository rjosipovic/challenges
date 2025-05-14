package com.playground.gamification_manager.game.messaging.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

@Value
public class ChallengeSolvedEvent {

    @UUID @NotNull
    String userId;

    @UUID @NotNull
    String challengeAttemptId;

    @NotNull @Range(min = 1, max = 9999)
    Integer firstNumber;

    @NotNull @Range(min = 1, max = 9999)
    Integer secondNumber;

    @NotNull
    Boolean correct;

    @NotNull @Pattern(regexp = "addition|subtraction|multiplication|division")
    String game;
}
