package com.playground.gamification_manager.game.service.model;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import lombok.Getter;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Getter
public class LeaderBoardItem {

    UUID userId;
    long totalScore;
    Set<BadgeType> badges;
}
