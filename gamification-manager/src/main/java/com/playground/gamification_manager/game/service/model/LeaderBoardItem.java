package com.playground.gamification_manager.game.service.model;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;

import java.util.Set;
import java.util.UUID;

public class LeaderBoardItem {

    private UUID userId;
    private int totalScore;
    private Set<BadgeType> badges;
}
