package com.playground.multiplication.challenge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ChallengeConfig {

    @Value("${app.challenge-generator.min}")
    private int min;
    @Value("${app.challenge-generator.max}")
    private int max;

    @Bean
    public ChallengeGeneratorService challengeGeneratorService() {
        return new ChallengeGeneratorServiceImpl(new Random(), min, max);
    }
}
