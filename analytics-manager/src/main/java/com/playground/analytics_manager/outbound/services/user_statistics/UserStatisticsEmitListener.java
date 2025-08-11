package com.playground.analytics_manager.outbound.services.user_statistics;

import com.playground.analytics_manager.inbound.messaging.events.ChallengeSolvedEvent;
import com.playground.analytics_manager.outbound.api.controllers.StatisticsController;
import com.playground.analytics_manager.outbound.api.dto.StatisticsUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatisticsEmitListener {

    private final StatisticsController statisticsController;

    @EventListener
    public void onProcessedChallengeSolvedEvent(ChallengeSolvedEvent event) {
        var userId = event.getUserId();
        var success = event.isCorrect();
        var game = event.getGame();
        var difficulty = event.getDifficulty();
        var statisticsUpdate = new StatisticsUpdate(userId, game, difficulty, success);
        statisticsController.publishStatisticsUpdate(statisticsUpdate);
    }
}
