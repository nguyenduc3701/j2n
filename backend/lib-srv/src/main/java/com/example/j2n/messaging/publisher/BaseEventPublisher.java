package com.example.j2n.messaging.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseEventPublisher {

    protected final RabbitTemplate rabbitTemplate;

    protected void publish(String exchange, String routingKey, Object event) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        log.info("[EVENT-PUBLISH] exchange={}, routingKey={}, correlationId={}", exchange, routingKey,
                correlationData.getId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event, correlationData);
    }
}
