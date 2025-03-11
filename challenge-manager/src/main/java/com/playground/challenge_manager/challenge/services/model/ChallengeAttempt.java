package com.playground.challenge_manager.challenge.services.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ChallengeAttempt {

    private String id;
    private UUID userId;
    private int factorA;
    private int factorB;
    private int resultAttempt;
    private boolean correct;
}
