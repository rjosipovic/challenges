package com.playground.gamification_manager.game.service.impl.challengesolved.chain.handlers;

import com.playground.gamification_manager.game.dataaccess.repositories.ScoreRepository;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedHandler;
import com.playground.gamification_manager.game.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TotalScoreHandler implements ChallengeSolvedHandler {

    private static final String LEADERBOARD_KEY = "leaderboard";

    private final ScoreRepository scoreRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean shouldHandle(ChallengeSolvedContext ctx) {
        return MathUtil.isPositive(ctx.getScore());
    }

    @Override
    public void handle(ChallengeSolvedContext context) {
        var userId = context.getUserId();
        var totalScore = scoreRepository.totalScoreByUserId(UUID.fromString(userId));
        updateLeaderboard(userId, totalScore);
    }

    private void updateLeaderboard(String userId, long totalScore) {
        var zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(LEADERBOARD_KEY, userId, totalScore);
    }
}
