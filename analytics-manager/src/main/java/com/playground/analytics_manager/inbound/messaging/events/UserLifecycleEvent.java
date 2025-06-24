package com.playground.analytics_manager.inbound.messaging.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserLifecycleEvent {

    private final User user;
    private final LifecycleType lifecycleType;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserLifecycleEvent(
            @JsonProperty("user") User user,
            @JsonProperty("lifecycleType") LifecycleType lifecycleType
    ) {
        this.user = user;
        this.lifecycleType = lifecycleType;
    }

    @Getter
    public static class User {
        private final String id;
        private final String alias;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public User(
                @JsonProperty("id") String id,
                @JsonProperty("alias") String alias
        ) {
            this.id = id;
            this.alias = alias;
        }
    }

    public enum LifecycleType {
        CREATED,
        UPDATED,
        DELETED
    }
}
