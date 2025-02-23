package com.playground.multiplication.challenge.services.impl.challengeservice.chain.config;

import com.playground.multiplication.challenge.services.impl.challengeservice.chain.AttemptVerifierChain;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.AttemptResultHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.CheckResultHandler;
import com.playground.multiplication.challenge.services.impl.challengeservice.chain.handlers.SaveAttemptHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AttemptVerifierConfig {

    private final SaveAttemptHandler saveAttemptHandler;

    @Bean
    public AttemptVerifierChain attemptVerifierChain() {
        var chain = new AttemptVerifierChain();
        chain.addHandler(new CheckResultHandler());
        chain.addHandler(saveAttemptHandler);
        chain.addHandler(new AttemptResultHandler());
        return chain;
    }
}
