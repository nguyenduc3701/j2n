package com.example.j2n.payment_srv.messaging.order.config;

import com.example.j2n.payment_srv.messaging.order.constant.OrderEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventConfig {

    @Bean
    public TopicExchange paymentOrderExchange() {
        return new TopicExchange(OrderEventConstants.EXCHANGE_ORDER);
    }

    @Bean
    public Queue paymentOrderSyncQueue() {
        return QueueBuilder.durable(OrderEventConstants.QUEUE_PAYMENT_ORDER_SYNC).build();
    }

    @Bean
    public Binding paymentOrderBinding() {
        return BindingBuilder
                .bind(paymentOrderSyncQueue())
                .to(paymentOrderExchange())
                .with(OrderEventConstants.RK_ORDER_ALL);
    }
}
