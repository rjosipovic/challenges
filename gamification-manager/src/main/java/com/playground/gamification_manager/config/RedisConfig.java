package com.playground.gamification_manager.config;

import com.playground.gamification_manager.game.messaging.MessagingConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final MessagingConfiguration messagingConfiguration;

    @Bean
    public TopicExchange challengeExchange() {
        var name = messagingConfiguration.getChallenge().getExchange();
        return ExchangeBuilder.topicExchange(name).durable(true).build();
    }

    @Bean
    public Queue challengeSolvedCorrectQueue() {
        var name = messagingConfiguration.getChallenge().getQueue();
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Binding challengeSolvedCorrectBinding() {
        var routingKey = messagingConfiguration.getChallenge().getChallengeCorrectRoutingKey();
        return BindingBuilder.bind(challengeSolvedCorrectQueue()).to(challengeExchange()).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        var redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
