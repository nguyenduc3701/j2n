package com.example.j2n.order_srv.messaging.room.config;

import com.example.j2n.order_srv.messaging.room.constant.RoomEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoomEventConfig {

    @Bean
    public TopicExchange roomExchange() {
        return new TopicExchange(RoomEventConstants.EXCHANGE_ROOM);
    }

    @Bean
    public Queue roomSyncQueue() {
        return QueueBuilder.durable(RoomEventConstants.QUEUE_ORDER_ROOM_SYNC).build();
    }

    @Bean
    public Binding roomBinding() {
        return BindingBuilder
                .bind(roomSyncQueue())
                .to(roomExchange())
                .with(RoomEventConstants.RK_ROOM_BILL_SYNCED);
    }
}
