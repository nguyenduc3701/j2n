package com.example.j2n.report_srv.messaging.payment.config;

import com.example.j2n.report_srv.messaging.payment.constant.PaymentEventConstants;
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
    public Queue reportPaymentConfirmedQueue() {
        return QueueBuilder.durable(PaymentEventConstants.QUEUE_REPORT_PAYMENT_CONFIRMED).build();
    }

    @Bean
    public Binding reportPaymentConfirmedBinding() {
        return BindingBuilder
                .bind(reportPaymentConfirmedQueue())
                .to(paymentExchange())
                .with(PaymentEventConstants.RK_PAYMENT_CONFIRMED);
    }
}
