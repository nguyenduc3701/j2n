package com.example.j2n.payment_srv.messaging.payment.config;

import com.example.j2n.payment_srv.messaging.payment.constant.PaymentEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentEventConfig {

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PaymentEventConstants.EXCHANGE_PAYMENT);
    }
}
