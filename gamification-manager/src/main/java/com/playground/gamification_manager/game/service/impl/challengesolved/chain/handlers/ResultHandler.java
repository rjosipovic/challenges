package com.playground.gamification_manager.game.service.impl.challengesolved.chain.handlers;

import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedHandler;
import com.playground.gamification_manager.game.service.model.GameResult;
import org.springframework.stereotype.Service;

@Service
public class ResultHandler implements ChallengeSolvedHandler {

    @Override
    public void handle(ChallengeSolvedContext ctx) {
        var userId = ctx.getUserId();
        var score = ctx.getScore();
        var badges = ctx.getBadges();
        var result = new GameResult(userId, score, badges);
        ctx.setResult(result);
    }
}
