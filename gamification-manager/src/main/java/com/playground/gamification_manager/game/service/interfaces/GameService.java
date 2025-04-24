package com.playground.gamification_manager.game.service.interfaces;

import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.service.model.GameResult;

public interface GameService {

    GameResult process(ChallengeSolvedDTO challengeSolved);
}
