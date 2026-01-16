package com.example.j2n.messaging.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@Primary
public class RabbitTemplateConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jacksonMessageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("[RABBIT-CONFIRM-OK] correlation={}, cause={}",
                        correlationData != null ? correlationData.getId() : null,
                        cause);
            } else {
                log.error("[RABBIT-CONFIRM-FAIL] correlation={}, cause={}",
                        correlationData != null ? correlationData.getId() : null,
                        cause);
            }
        });

        // return: routing fail
        template.setReturnsCallback(returned -> {
            log.error("[RABBIT-RETURN] exchange={}, routingKey={}, replyText={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText());
        });
        return template;
    }

}
