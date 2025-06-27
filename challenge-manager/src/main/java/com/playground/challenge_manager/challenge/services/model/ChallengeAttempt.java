package com.playground.challenge_manager.challenge.services.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ChallengeAttempt {

    private UUID challengeAttemptId;
    private UUID userId;
    private int firstNumber;
    private int secondNumber;
    private int resultAttempt;
    private boolean correct;
    private String game;
    private String difficulty;
    private ZonedDateTime attemptDate;

    public ChallengeAttempt withChallengeAttemptIdAndAttemptDate(UUID challengeAttemptId, ZonedDateTime attemptDate) {
        return new ChallengeAttempt(challengeAttemptId, userId, firstNumber, secondNumber, resultAttempt, correct, game, difficulty, attemptDate);
    }
}
