package com.playground.gamification_manager.game.service.impl.challengesolved.chain;

import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.dataaccess.domain.BadgeType;
import com.playground.gamification_manager.game.service.model.GameResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class ChallengeSolvedContext {

    private final String userId;
    private final String challengeAttemptId;
    private final int firstNumber;
    private final int secondNumber;
    private final boolean correct;
    private final String game;
    @Setter
    private String difficulty;
    @Setter
    private int score;
    private final Set<BadgeType> badges;
    @Setter
    private GameResult result;

    public ChallengeSolvedContext(ChallengeSolvedDTO challengeSolved) {
        this.userId = challengeSolved.getUserId();
        this.challengeAttemptId = challengeSolved.getChallengeAttemptId();
        this.firstNumber = challengeSolved.getFirstNumber();
        this.secondNumber = challengeSolved.getSecondNumber();
        this.correct = challengeSolved.getCorrect();
        this.game = challengeSolved.getGame();
        this.difficulty = null;
        this.score = 0;
        this.badges = new HashSet<>();
    }

    public void addBadge(BadgeType badgeType) {
        badges.add(badgeType);
    }

    public void addBadges(Set<BadgeType> badges) {
        this.badges.addAll(badges);
    }

    public Set<BadgeType> getBadges() { return Collections.unmodifiableSet(badges);  }
}
