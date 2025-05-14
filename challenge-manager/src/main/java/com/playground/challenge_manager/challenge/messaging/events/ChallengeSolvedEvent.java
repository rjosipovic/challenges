package com.playground.challenge_manager.challenge.messaging.events;

import lombok.Value;

@Value
public class ChallengeSolvedEvent {

    String userId;
    String challengeAttemptId;
    int firstNumber;
    int secondNumber;
    boolean correct;
    String game;
}
