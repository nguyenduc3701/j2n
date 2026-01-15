package com.example.j2n.image_srv.config;

import com.example.j2n.messaging.constant.UserEventConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitMQConfig {
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(UserEventConstants.EXCHANGE_USER);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jacksonMessageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("✅ Event {} published successfully",
                        correlationData != null ? correlationData.getId() : "unknown");
            } else {
                log.error("❌ Failed to publish event {}", correlationData != null ? correlationData.getId() : "unknown",
                        cause);
            }
        });
        return template;
    }
}
