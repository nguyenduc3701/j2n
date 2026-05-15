package com.example.j2n.report_srv.messaging.room.config;

import com.example.j2n.report_srv.messaging.room.constant.RoomEventConstants;
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
    public Queue roomStatusUpdatedQueue() {
        return QueueBuilder.durable(RoomEventConstants.QUEUE_REPORT_ROOM_STATUS_UPDATED).build();
    }

    @Bean
    public Binding roomStatusUpdatedBinding() {
        return BindingBuilder
                .bind(roomStatusUpdatedQueue())
                .to(roomExchange())
                .with(RoomEventConstants.RK_ROOM_STATUS_UPDATED);
    }

    @Bean
    public Queue reportBillsCalculatedQueue() {
        return QueueBuilder.durable(RoomEventConstants.QUEUE_REPORT_BILLS_CALCULATED).build();
    }

    @Bean
    public Binding reportBillsCalculatedBinding() {
        return BindingBuilder
                .bind(reportBillsCalculatedQueue())
                .to(roomExchange())
                .with(RoomEventConstants.RK_BILLS_CALCULATED);
    }
}
