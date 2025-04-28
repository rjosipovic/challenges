package com.playground.gamification_manager.game.service.impl;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeEntity;
import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import com.playground.gamification_manager.game.dataaccess.domain.UserScore;
import com.playground.gamification_manager.game.dataaccess.repositories.BadgeRepository;
import com.playground.gamification_manager.game.dataaccess.repositories.ScoreRepository;
import com.playground.gamification_manager.game.service.model.LeaderBoardItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderBoardServiceImplTest {

    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private LeaderBoardServiceImpl leaderBoardService;

    @Test
    void shouldGetLeaderBoard() {
        //given
        var firstUserId = UUID.randomUUID();
        var secondUserId = UUID.randomUUID();
        var thirdUserId = UUID.randomUUID();
        var firstTotalScore = 750;
        var secondTotalScore = 500;
        var thirdTotalScore = 250;
        var firstBadges = Set.of(BadgeType.FIRST_WON, BadgeType.BRONZE, BadgeType.SILVER, BadgeType.GOLD);
        var secondBadges = Set.of(BadgeType.FIRST_WON, BadgeType.BRONZE, BadgeType.SILVER);
        var thirdBadges = Set.of(BadgeType.FIRST_WON, BadgeType.BRONZE);
        var first = new LeaderBoardItem(firstUserId, firstTotalScore, firstBadges);
        var second = new LeaderBoardItem(secondUserId, secondTotalScore, secondBadges);
        var third = new LeaderBoardItem(thirdUserId, thirdTotalScore, thirdBadges);

        var firstFirstWonBadge = new BadgeEntity(UUID.randomUUID(), firstUserId, BadgeType.FIRST_WON, ZonedDateTime.now());
        var firstBronzeBadge = new BadgeEntity(UUID.randomUUID(), firstUserId, BadgeType.BRONZE, ZonedDateTime.now());
        var firstSilverBadge = new BadgeEntity(UUID.randomUUID(), firstUserId, BadgeType.SILVER, ZonedDateTime.now());
        var firstGoldBadge = new BadgeEntity(UUID.randomUUID(), firstUserId, BadgeType.GOLD, ZonedDateTime.now());
        var secondFirstWonBadge = new BadgeEntity(UUID.randomUUID(), secondUserId, BadgeType.FIRST_WON, ZonedDateTime.now());
        var secondBronzeBadge = new BadgeEntity(UUID.randomUUID(), secondUserId, BadgeType.BRONZE, ZonedDateTime.now());
        var secondSilverBadge = new BadgeEntity(UUID.randomUUID(), secondUserId, BadgeType.SILVER, ZonedDateTime.now());
        var thirdFirstWonBadge = new BadgeEntity(UUID.randomUUID(), thirdUserId, BadgeType.FIRST_WON, ZonedDateTime.now());
        var thirdBronzeBadge = new BadgeEntity(UUID.randomUUID(), thirdUserId, BadgeType.BRONZE, ZonedDateTime.now());
        var firstUserScore = new UserScore(firstUserId, firstTotalScore);
        var secondUserScore = new UserScore(secondUserId, secondTotalScore);
        var thirdUserScore = new UserScore(thirdUserId, thirdTotalScore);

        when(scoreRepository.findFirst10()).thenReturn(List.of(firstUserScore, secondUserScore, thirdUserScore));
        when(badgeRepository.findAllByUserId(firstUserId)).thenReturn(List.of(firstFirstWonBadge, firstBronzeBadge, firstSilverBadge, firstGoldBadge));
        when(badgeRepository.findAllByUserId(secondUserId)).thenReturn(List.of(secondFirstWonBadge, secondBronzeBadge, secondSilverBadge));
        when(badgeRepository.findAllByUserId(thirdUserId)).thenReturn(List.of(thirdFirstWonBadge, thirdBronzeBadge));

        //when
        var leaderBoard = leaderBoardService.getLeaderBoard();

        //then
        assertAll(
                () -> assertEquals(3, leaderBoard.size()),
                () -> assertEquals(first, leaderBoard.get(0)),
                () -> assertEquals(second, leaderBoard.get(1)),
                () -> assertEquals(third, leaderBoard.get(2))
        );
        verify(scoreRepository).findFirst10();
        verify(badgeRepository).findAllByUserId(firstUserId);
        verify(badgeRepository).findAllByUserId(secondUserId);
        verify(badgeRepository).findAllByUserId(thirdUserId);
    }
}