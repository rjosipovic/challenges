package com.playground.gamification_manager.game.api.validation;

import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.util.MathUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SameDigitCountValidator implements ConstraintValidator<SameDigitCount, ChallengeSolvedDTO> {

    @Override
    public boolean isValid(ChallengeSolvedDTO value, ConstraintValidatorContext context) {
        if (value.getFirstNumber() == null || value.getSecondNumber() == null) {
            return true; // let @NotNull handle this
        }
        return MathUtil.getDigitCount(value.getFirstNumber()) == MathUtil.getDigitCount(value.getSecondNumber());
    }
}
