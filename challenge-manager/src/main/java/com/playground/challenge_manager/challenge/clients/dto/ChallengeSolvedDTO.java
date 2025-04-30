package com.playground.challenge_manager.challenge.clients.dto;

import lombok.Value;

@Value
public class ChallengeSolvedDTO {

    String userId;
    String challengeAttemptId;
    int firstNumber;
    int secondNumber;
    boolean correct;
    String game;
}
