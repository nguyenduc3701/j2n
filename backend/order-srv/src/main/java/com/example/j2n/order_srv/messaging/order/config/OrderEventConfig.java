package com.example.j2n.order_srv.messaging.order.config;

import com.example.j2n.order_srv.messaging.order.constant.OrderEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventConfig {

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(OrderEventConstants.EXCHANGE_ORDER);
    }
}
