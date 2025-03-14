package com.playground.challenge_manager.challenge.services.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Challenge {

    private final int firstNumber;
    private final int secondNumber;
}
