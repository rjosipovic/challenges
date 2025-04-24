package com.playground.gamification_manager.game.util;

public final class MathUtil {

    private MathUtil() {
        throw new AssertionError("Utility class can not be instantiated");
    }

    public static int getDigitCount(int number) {
        if (number == 0) return 1;
        return (int) Math.log10(Math.abs(number)) + 1;
    }

    public static boolean isPositive(int number) {
        return number > 0;
    }
}
