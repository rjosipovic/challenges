package com.playground.analytics_manager.outbound.services.challenge_history.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtil {

    public static int calculateResult(int firstNumber, int secondNumber, String game) {
        return switch (game) {
            case "addition" -> firstNumber + secondNumber;
            case "subtraction" -> firstNumber - secondNumber;
            case "multiplication" -> firstNumber * secondNumber;
            case "division" -> firstNumber / secondNumber;
            default -> 0;
        };
    }
}
