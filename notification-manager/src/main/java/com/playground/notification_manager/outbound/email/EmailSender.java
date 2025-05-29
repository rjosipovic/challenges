package com.playground.notification_manager.outbound.email;

import com.playground.notification_manager.model.Notification;
import com.playground.notification_manager.outbound.email.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;

    public void sendEmail(Notification notification) {
        var to = notification.getTo();
        var from = emailConfig.getDefaultFrom();
        var subject = notification.getSubject();
        var body = notification.getBody();
        var message = buildSimpleMailMessage(to, from, subject, body);
        mailSender.send(message);
    }

    private static SimpleMailMessage buildSimpleMailMessage(
            String to,
            String from,
            String subject,
            String body
    ) {
        if (Objects.isNull(to) || Objects.isNull(from) || Objects.isNull(subject) || Objects.isNull(body)) {
            throw new IllegalArgumentException("EmailNotification cannot be null");
        }
        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}
