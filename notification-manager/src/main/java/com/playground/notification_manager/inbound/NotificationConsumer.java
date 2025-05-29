package com.playground.notification_manager.inbound;

import com.playground.notification_manager.model.Notification;
import com.playground.notification_manager.outbound.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailSender emailSender;

    @RabbitListener(queues = "#{notificationQueue.name}", ackMode = "AUTO")
    public void consume(Notification notification) {
        emailSender.sendEmail(notification);
    }
}
