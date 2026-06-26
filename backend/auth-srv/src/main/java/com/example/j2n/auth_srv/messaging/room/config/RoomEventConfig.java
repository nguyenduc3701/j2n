package com.example.j2n.auth_srv.messaging.room.config;

import com.example.j2n.auth_srv.messaging.room.constant.RoomEventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoomEventConfig {

    @Bean
    public TopicExchange roomExchange() {
        return new TopicExchange(RoomEventConstants.EXCHANGE_ROOM);
    }

    @Bean
    public Queue roomMemberRemovedQueue() {
        return QueueBuilder.durable(RoomEventConstants.QUEUE_AUTH_ROOM_MEMBER_REMOVED).build();
    }

    @Bean
    public Binding roomMemberRemovedBinding() {
        return BindingBuilder
                .bind(roomMemberRemovedQueue())
                .to(roomExchange())
                .with(RoomEventConstants.RK_ROOM_MEMBER_REMOVED);
    }

    @Bean
    public Queue roomMemberMappedQueue() {
        return QueueBuilder.durable(RoomEventConstants.QUEUE_AUTH_ROOM_MEMBER_MAPPED).build();
    }

    @Bean
    public Binding roomMemberMappedBinding() {
        return BindingBuilder
                .bind(roomMemberMappedQueue())
                .to(roomExchange())
                .with(RoomEventConstants.RK_ROOM_MEMBER_MAPPED);
    }
}
