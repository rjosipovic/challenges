package com.playground.user_manager.user.producers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.messaging")
@Configuration
public class MessagingConfiguration {

    @Getter @Setter
    private UserMessageConfiguration user;

    @Getter @Setter
    @NoArgsConstructor
    public static class UserMessageConfiguration {
        private String exchange;
        private String userCreatedRoutingKey;
    }
}
