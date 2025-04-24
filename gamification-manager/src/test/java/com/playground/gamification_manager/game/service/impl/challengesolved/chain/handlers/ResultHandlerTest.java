package com.playground.gamification_manager.game.service.impl.challengesolved.chain.handlers;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.model.GameResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultHandlerTest {

    @Mock
    private ChallengeSolvedContext ctx;

    private final ResultHandler resultHandler = new ResultHandler();

    @Test
    void shouldSetResult() {
        //given
        var userId = UUID.randomUUID().toString();
        var score = 10;
        var badges = Set.of(BadgeType.FIRST_WON);
        when(ctx.getUserId()).thenReturn(userId);
        when(ctx.getScore()).thenReturn(score);
        when(ctx.getBadges()).thenReturn(badges);
        var result = new GameResult(userId, score, badges);
        //when
        resultHandler.handle(ctx);
        //then
        verify(ctx).setResult(result);
    }
}