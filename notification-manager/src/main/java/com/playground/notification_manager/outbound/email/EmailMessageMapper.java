package com.playground.notification_manager.outbound.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EmailMessageMapper {

    public SimpleMailMessage toSimpleMailMessage(EmailMessage emailMessage) {
        Objects.requireNonNull(emailMessage, "EmailMessage cannot be null.");
        String to = emailMessage.getTo();
        String from = emailMessage.getFrom();
        String subject = emailMessage.getSubject();
        String body = emailMessage.getBody();

        if (Objects.isNull(to) || Objects.isNull(from) || Objects.isNull(subject) || Objects.isNull(body)) {
            throw new IllegalArgumentException("Email 'to', 'from', 'subject', and 'body' cannot be null.");
        }

        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}