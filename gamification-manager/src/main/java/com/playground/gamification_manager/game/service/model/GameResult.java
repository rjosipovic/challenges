package com.playground.gamification_manager.game.service.model;

import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import lombok.Value;

import java.util.Set;

@Value
public class GameResult {

    String userId;
    int score;
    Set<BadgeType> badges;
}
