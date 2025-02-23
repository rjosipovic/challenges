package com.playground.multiplication.challenge.services.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ChallengeAttempt {

    private String id;
    private String userId;
    private int factorA;
    private int factorB;
    private int resultAttempt;
    private boolean correct;
}
