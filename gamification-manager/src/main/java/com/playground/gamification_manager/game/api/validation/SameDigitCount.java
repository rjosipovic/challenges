package com.playground.gamification_manager.game.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SameDigitCountValidator.class)
public @interface SameDigitCount {
    String message() default "Numbers must have the same digit count";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}