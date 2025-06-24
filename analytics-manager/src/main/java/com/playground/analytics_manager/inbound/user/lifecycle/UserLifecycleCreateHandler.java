package com.playground.analytics_manager.inbound.user.lifecycle;

import com.playground.analytics_manager.inbound.user.model.User;
import com.playground.analytics_manager.inbound.user.model.UserLifecycleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLifecycleCreateHandler implements UserLifecycleHandler {

    @Override
    public void handle(User user) {
        log.info("User created: {}", user);
    }

    @Override
    public UserLifecycleType supports() {
        return UserLifecycleType.CREATED;
    }
}
