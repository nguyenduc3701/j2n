package com.example.j2n.image_srv.messaging.travel.config;

import com.example.j2n.image_srv.messaging.travel.constant.TravelEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TravelEventConfig {
    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TravelEventConstants.EXCHANGE_TRAVEL);
    }
}
