package com.example.j2n.report_srv.messaging.product.config;

import com.example.j2n.report_srv.messaging.product.constant.TravelEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TravelEventConfig {

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(TravelEventConstants.EXCHANGE_PRODUCT);
    }

    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(TravelEventConstants.QUEUE_REPORT_PRODUCT_TOUR).build();
    }

    @Bean
    public Binding productBinding() {
        return BindingBuilder
                .bind(productQueue())
                .to(productExchange())
                .with(TravelEventConstants.RK_TOUR_ALL);
    }
}
