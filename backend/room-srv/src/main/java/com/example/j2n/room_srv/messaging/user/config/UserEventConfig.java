package com.example.j2n.room_srv.messaging.user.config;

import com.example.j2n.room_srv.messaging.user.constant.UserEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserEventConfig {

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(UserEventConstants.EXCHANGE_USER);
    }

    @Bean
    public Queue roomUserRegisteredQueue() {
        return QueueBuilder.durable(UserEventConstants.QUEUE_ROOM_USER_REGISTERED).build();
    }

    @Bean
    public Binding roomUserRegisteredBinding() {
        return BindingBuilder
                .bind(roomUserRegisteredQueue())
                .to(userExchange())
                .with(UserEventConstants.RK_USER_REGISTERED);
    }

    @Bean
    public Queue roomUserUpdatedQueue() {
        return QueueBuilder.durable(UserEventConstants.QUEUE_ROOM_USER_UPDATED).build();
    }

    @Bean
    public Binding roomUserUpdatedBinding() {
        return BindingBuilder
                .bind(roomUserUpdatedQueue())
                .to(userExchange())
                .with(UserEventConstants.RK_USER_UPDATED);
    }

    @Bean
    public Queue roomUserDeletedQueue() {
        return QueueBuilder.durable(UserEventConstants.QUEUE_ROOM_USER_DELETED).build();
    }

    @Bean
    public Binding roomUserDeletedBinding() {
        return BindingBuilder
                .bind(roomUserDeletedQueue())
                .to(userExchange())
                .with(UserEventConstants.RK_USER_DELETED);
    }
}
