package com.example.j2n.room_srv.messaging.payment.config;

import com.example.j2n.room_srv.messaging.payment.constant.PaymentEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentEventConfig {

    @Bean
    public Queue roomPaymentConfirmedQueue() {
        return QueueBuilder.durable(PaymentEventConstants.QUEUE_ROOM_PAYMENT_CONFIRMED).build();
    }

    @Bean
    public Binding roomPaymentBinding() {
        return BindingBuilder
                .bind(roomPaymentConfirmedQueue())
                .to(new TopicExchange(PaymentEventConstants.EXCHANGE_PAYMENT))
                .with(PaymentEventConstants.RK_PAYMENT_CONFIRMED);
    }
}
