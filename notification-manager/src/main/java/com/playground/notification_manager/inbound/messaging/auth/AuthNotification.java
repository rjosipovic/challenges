package com.playground.notification_manager.inbound.messaging.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthNotification {

    private String to;
    private String subject;
    private String body;
}
