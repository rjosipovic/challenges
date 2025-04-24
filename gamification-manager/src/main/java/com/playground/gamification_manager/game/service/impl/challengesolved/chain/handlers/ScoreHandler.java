package com.playground.gamification_manager.game.service.impl.challengesolved.chain.handlers;

import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedHandler;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.config.DifficultyLevelsConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreHandler implements ChallengeSolvedHandler {

    private final DifficultyLevelsConfiguration difficultyLevelsConfiguration;

    @Override
    public boolean shouldHandle(ChallengeSolvedContext ctx) {
        return ctx.isCorrect();
    }

    @Override
    public void handle(ChallengeSolvedContext ctx) {
        var difficulty = ctx.getDifficulty();
        var score = getScoreFromDifficulty(difficulty);
        ctx.setScore(score);
    }

    private int getScoreFromDifficulty(String difficulty) {
        return Optional.ofNullable(difficultyLevelsConfiguration.getScoreMap().get(difficulty)).orElseGet(() -> 0);
    }
}
