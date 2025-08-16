package com.playground.analytics_manager.outbound.api.dto;

import lombok.Value;

@Value
public class ChallengeResult {

    String alias;
    Integer firstNumber;
    Integer secondNumber;
    Integer guess;
    Integer correctResult;
    Boolean correct;
    String game;
}
