package com.playground.analytics_manager.inbound.challenge;

import com.playground.analytics_manager.inbound.messaging.events.ChallengeSolvedEvent;
import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Override
    public void process(ChallengeSolvedEvent event) {

    }
}
