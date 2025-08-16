package com.playground.notification_manager.outbound.email;

import lombok.Value;

@Value
public class EmailMessage {

    String from;
    String to;
    String subject;
    String body;
}
