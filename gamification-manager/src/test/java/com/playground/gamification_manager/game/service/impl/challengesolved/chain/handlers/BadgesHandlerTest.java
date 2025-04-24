package com.playground.gamification_manager.game.service.impl.challengesolved.chain.handlers;

import com.playground.gamification_manager.game.dataaccess.repositories.BadgeRepository;
import com.playground.gamification_manager.game.dataaccess.repositories.ScoreRepository;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.interfaces.BadgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgesHandlerTest {

    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private BadgeRepository badgeRepository;
    @Mock
    private BadgeService badgeService;
    @Mock
    private ChallengeSolvedContext ctx;

    @InjectMocks
    private BadgesHandler badgesHandler;

    @Test
    void shouldHandle() {
        //given
        when(ctx.isCorrect()).thenReturn(true);
        //when
        var result = badgesHandler.shouldHandle(ctx);
        //then
        assertTrue(result);
    }

    @Test
    void shouldNotHandle() {
        //given
        when(ctx.isCorrect()).thenReturn(false);
        //when
        var result = badgesHandler.shouldHandle(ctx);
        //then
        assertFalse(result);
    }

//    @Test
//    void shouldDetermineNewBadges() {
//        //given
//        var userId = UUID.randomUUID();
//        when(ctx.getUserId()).thenReturn(userId.toString());
//        var score = 10;
//        var firstNumber = 11;
//        var secondNumber = 12;
//        var currentScore = 90;
//        var currentBadges = Set.of();
//        when(scoreRepository.totalScoreByUserId(userId)).thenReturn(currentScore);
//        when(badgeRepository.findAllByUserId(userId)).thenReturn(currentBadges);
//        when(ctx.getScore()).thenReturn(score);
//        when(ctx.getFirstNumber()).thenReturn(firstNumber);
//        when(ctx.getSecondNumber()).thenReturn(secondNumber);
//        when(badgeService.determineBadges(new BadgesContext(score, currentScore, currentBadges, firstNumber, secondNumber))).thenReturn(Set.of(BadgeType.BRONZE));
//        //when
//        badgesHandler.handle(ctx);
//        //then
//        verify(ctx).addBadges(Set.of(BadgeType.BRONZE));
//    }
}