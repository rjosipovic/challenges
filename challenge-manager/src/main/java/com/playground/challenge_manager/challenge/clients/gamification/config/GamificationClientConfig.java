package com.playground.challenge_manager.challenge.clients.gamification.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.clients.gamification")
@Getter
@Setter
@NoArgsConstructor
public class GamificationClientConfig {

    private String protocol;
    private String host;
    private String port;
    private String publishAttemptPath;
}
