package com.example.j2n.report_srv.messaging.user.config;

import com.example.j2n.report_srv.messaging.user.constant.UserEventConstants;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;

@Configuration
public class UserEventConfig {
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
