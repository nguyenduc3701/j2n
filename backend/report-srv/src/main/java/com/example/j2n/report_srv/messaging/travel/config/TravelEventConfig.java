package com.example.j2n.report_srv.messaging.travel.config;

import com.example.j2n.report_srv.messaging.travel.constant.TravelEventConstants;
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
    public Queue tourQueue() {
        return QueueBuilder.durable(TravelEventConstants.QUEUE_REPORT_TRAVEL_TOUR).build();
    }

    @Bean
    public Binding tourBinding() {
        return BindingBuilder
                .bind(tourQueue())
                .to(travelExchange())
                .with(TravelEventConstants.RK_TOUR_ALL);
    }
}
