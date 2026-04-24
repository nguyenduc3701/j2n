package com.example.j2n.payment_srv.messaging.reservation.config;

import com.example.j2n.payment_srv.messaging.reservation.constant.ReservationConstants;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReservationConfig {

    @Value("${spring.rabbitmq.dead-letter-message-ttl}")
    private long deadLetterMessageTtl;

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(ReservationConstants.EXCHANGE_RESERVATION);
    }

    @Bean
    public Queue reservationDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ReservationConstants.EXCHANGE_RESERVATION);
        args.put("x-dead-letter-routing-key", ReservationConstants.RK_RESERVATION_RELEASE);
        args.put("x-message-ttl", deadLetterMessageTtl);
        return QueueBuilder.durable(ReservationConstants.QUEUE_RESERVATION_DELAY)
                .withArguments(args)
                .build();
    }

    @Bean
    public Binding reservationDelayBinding() {
        return BindingBuilder
                .bind(reservationDelayQueue())
                .to(reservationExchange())
                .with(ReservationConstants.RK_RESERVATION_DELAY);
    }

    @Bean
    public Queue reservationReleaseQueue() {
        return QueueBuilder.durable(ReservationConstants.QUEUE_RESERVATION_RELEASE).build();
    }

    @Bean
    public Binding reservationReleaseBinding() {
        return BindingBuilder
                .bind(reservationReleaseQueue())
                .to(reservationExchange())
                .with(ReservationConstants.RK_RESERVATION_RELEASE);
    }
}
