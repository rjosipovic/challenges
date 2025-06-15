package com.playground.gamification_manager.game.startup;

import com.playground.gamification_manager.game.dataaccess.repositories.ScoreRepository;
import com.playground.gamification_manager.game.service.impl.leaderboard.LeaderBoardConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitLeaderBoard {

    private final RedisTemplate<String, Object> redisTemplate;
    private final LeaderBoardConfiguration leaderBoardConfiguration;
    private final ScoreRepository scoreRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        removeLeaderBoardIfPresent();
        setLeaderBoard();
    }

    private void setLeaderBoard() {
        var key = leaderBoardConfiguration.getKey();
        scoreRepository.totalScorePerUser().forEach(user -> {
            var userId = user.getUserId().toString();
            var totalScore = user.getTotalScore();
            var zSetOps = redisTemplate.opsForZSet();
            zSetOps.add(key, userId, totalScore);
        });
    }

    private void removeLeaderBoardIfPresent() {
        var key = leaderBoardConfiguration.getKey();
        redisTemplate.unlink(key);
    }
}
