package com.playground.challenge_manager.challenge.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("app.messaging")
@Configuration
public class MessagingConfiguration {

    @Getter @Setter
    private ChallengeConfiguration challenge;

    @NoArgsConstructor
    @Getter @Setter
    public static class ChallengeConfiguration {
        private String exchange;
        private String challengeCorrectRoutingKey;
        private String challengeFailedRoutingKey;
    }
}
