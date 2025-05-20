package com.playground.challenge_manager.messaging.callback;

import com.playground.challenge_manager.challenge.messaging.MessagingConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackManager {

    private static final int MAX_RETRY_COUNT = 3;

    private final ConcurrentMap<String, Message> pendingMessages = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessagingConfiguration messagingConfiguration;

    public void processCallback(CorrelationData correlationData, boolean ack) {
        var correlationId = correlationData.getId();
        if (ack) {
            processSuccessAck(correlationId);
        } else {
            processNegativeAck(correlationId);
        }
    }

    private void processSuccessAck(String correlationId) {
        log.info("Received ack for correlationId: {}", correlationId);
        pendingMessages.remove(correlationId);
    }

    private void processNegativeAck(String correlationId) {
        log.warn("Received nack for correlationId: {}", correlationId);
        var message = pendingMessages.get(correlationId);

        if (Objects.isNull(message)) {
            log.error("No pending message found for correlationId: {}. Cannot retry.", correlationId);
            return;
        }

        var messageProperties = message.getMessageProperties();
        if (Objects.isNull(messageProperties)) {
            log.error("Missing message properties for correlationId: {}. Cannot retry.", correlationId);
            return;
        }

        var retryCount = (Integer) Optional.ofNullable(messageProperties.getHeaders().get("x-retry-count"))
                .filter(value -> value instanceof Integer)
                .orElse(null);
        var exchange = Optional.ofNullable(messageProperties.getHeaders().get("x-exchange"))
                .filter(value -> value instanceof String)
                .map(Object::toString)
                .orElse(null);
        var routingKey = Optional.ofNullable(messageProperties.getHeaders().get("x-routing-key"))
                .filter(value -> value instanceof String)
                .map(Object::toString)
                .orElse(null);

        if (Objects.isNull(retryCount) ||
                Objects.isNull(exchange) ||
                Objects.isNull(routingKey)
        ) {
            log.error("Missing retry count, exchange or routing key for correlationId: {}. Cannot retry.", correlationId);
            return;
        }

        if (retryCount < MAX_RETRY_COUNT) {

            message.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
            applicationEventPublisher.publishEvent(new MessageRetryEvent(message, exchange, routingKey));
        } else {
            log.error("Message could not be retried for correlationId: {}. RetryCount: {}, Exchange: {}, RoutingKey: {}", correlationId, retryCount, exchange, routingKey);
            log.info("Publishing to dead letter queue");
            var dlxExchange = messagingConfiguration.getDeadLetter().getExchange();
            var dlxRoutingKey = messagingConfiguration.getDeadLetter().getRoutingKey();
            applicationEventPublisher.publishEvent(new MessageRetryEvent(message, dlxExchange, dlxRoutingKey));
            pendingMessages.remove(correlationId);
        }
    }

    public void put(String correlationId, Message m) {
        pendingMessages.put(correlationId, m);
    }
}
