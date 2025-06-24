package com.playground.user_manager.user.messaging;

import com.playground.user_manager.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserLifecycleEvent {

    private final User user;
    private final LifecycleType lifecycleType;
}
