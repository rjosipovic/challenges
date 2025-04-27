package com.playground.gamification_manager.game.service.impl;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeEntity;
import com.playground.gamification_manager.game.dataaccess.repositories.BadgeRepository;
import com.playground.gamification_manager.game.dataaccess.repositories.ScoreRepository;
import com.playground.gamification_manager.game.service.interfaces.LeaderBoardService;
import com.playground.gamification_manager.game.service.model.LeaderBoardItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private static final int MAX_LEADERS = 10;

    private final ScoreRepository scoreRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public List<LeaderBoardItem> getLeaderBoard() {
        Pageable pageable = PageRequest.of(0, MAX_LEADERS);
        var leaders = scoreRepository.sumScoresGroupedByUser(pageable);
        return leaders.stream()
                .map(leader -> {
                    var userId = leader.getUserId();
                    var badges = badgeRepository.findAllByUserId(userId)
                            .stream()
                            .map(BadgeEntity::getBadgeType)
                            .collect(Collectors.toSet());
                    return new LeaderBoardItem(userId, leader.getTotalScore(), badges);
                })
                .toList();
    }
}
