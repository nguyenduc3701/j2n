package com.example.j2n.travel_srv.messaging.travel.config;

import com.example.j2n.travel_srv.messaging.travel.constant.TravelEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TravelEventConfig {
    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TravelEventConstants.EXCHANGE_TRAVEL);
    }

    @Bean
    public Queue tourImageQueue() {
        return QueueBuilder.durable(TravelEventConstants.QUEUE_TRAVEL_TOUR_IMAGE).build();
    }

    @Bean
    public Binding tourImageBinding() {
        return BindingBuilder
                .bind(tourImageQueue())
                .to(travelExchange())
                .with(TravelEventConstants.RK_TOUR_IMAGE_UPLOADED);
    }
}
