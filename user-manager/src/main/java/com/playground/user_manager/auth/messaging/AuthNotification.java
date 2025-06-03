package com.playground.user_manager.auth.messaging;

import lombok.Value;

@Value
public class AuthNotification {

    String to;
    String subject;
    String body;
}
