package com.playground.analytics_manager.inbound.messaging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChallengeSolvedEvent {

    private final String userId;
    private final String challengeAttemptId;
    private final int firstNumber;
    private final int secondNumber;
    private final boolean correct;
    private final String game;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ChallengeSolvedEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("challengeAttemptId") String challengeAttemptId,
            @JsonProperty("firstNumber") int firstNumber,
            @JsonProperty("secondNumber") int secondNumber,
            @JsonProperty("correct") boolean correct,
            @JsonProperty("game") String game
    ) {
        this.userId = userId;
        this.challengeAttemptId = challengeAttemptId;
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.correct = correct;
        this.game = game;
    }
}
