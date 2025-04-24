package com.playground.challenge_manager.challenge.clients.dto;

import lombok.Value;

@Value
public class ChallengeSolvedDTO {

    String userId;
    int firstNumber;
    int secondNumber;
    boolean correct;
    String game;
}
