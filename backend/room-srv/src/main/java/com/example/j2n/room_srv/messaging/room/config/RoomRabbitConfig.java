package com.example.j2n.room_srv.messaging.room.config;

import com.example.j2n.room_srv.messaging.room.constant.RoomEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoomRabbitConfig {

    @Bean
    public TopicExchange roomExchange() {
        return new TopicExchange(RoomEventConstants.EXCHANGE_ROOM);
    }
}
