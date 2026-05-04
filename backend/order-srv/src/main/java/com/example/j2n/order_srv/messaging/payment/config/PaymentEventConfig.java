package com.example.j2n.order_srv.messaging.payment.config;

import com.example.j2n.order_srv.messaging.payment.constant.PaymentEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentEventConfig {

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PaymentEventConstants.EXCHANGE_PAYMENT);
    }

    @Bean
    public Queue orderPaymentConfirmedQueue() {
        return QueueBuilder.durable(PaymentEventConstants.QUEUE_ORDER_PAYMENT_CONFIRMED).build();
    }

    @Bean
    public Binding orderPaymentConfirmedBinding() {
        return BindingBuilder
                .bind(orderPaymentConfirmedQueue())
                .to(paymentExchange())
                .with(PaymentEventConstants.RK_PAYMENT_CONFIRMED);
    }
}
