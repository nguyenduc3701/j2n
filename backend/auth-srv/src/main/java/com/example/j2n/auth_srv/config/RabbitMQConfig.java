package com.example.j2n.auth_srv.config;

import com.example.j2n.messaging.constant.UserEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(UserEventConstants.EXCHANGE_USER);
    }

    @Bean
    public Queue avatarQueue() {
        return QueueBuilder.durable(UserEventConstants.QUEUE_AUTH_AVATAR).build();
    }

    @Bean
    public Binding avatarBinding() {
        return BindingBuilder
                .bind(avatarQueue())
                .to(userExchange())
                .with(UserEventConstants.RK_AVATAR_UPLOADED);
    }
}
