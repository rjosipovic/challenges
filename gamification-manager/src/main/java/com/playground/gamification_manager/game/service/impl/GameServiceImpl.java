package com.playground.gamification_manager.game.service.impl;

import com.playground.gamification_manager.game.api.dto.ChallengeSolvedDTO;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedChain;
import com.playground.gamification_manager.game.service.impl.challengesolved.chain.ChallengeSolvedContext;
import com.playground.gamification_manager.game.service.interfaces.GameService;
import com.playground.gamification_manager.game.service.model.GameResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final ChallengeSolvedChain challengeSolvedChain;

    @Override
    @Transactional
    public GameResult process(ChallengeSolvedDTO challengeSolved) {
        var ctx = new ChallengeSolvedContext(challengeSolved);
        challengeSolvedChain.handle(ctx);
        return ctx.getResult();
    }
}
