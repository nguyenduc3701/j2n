package com.example.j2n.notification_srv.messaging.config;

import com.example.j2n.notification_srv.messaging.constant.NotificationEventConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationEventConfig {

    @Bean
    public TopicExchange roomExchangeForNotification() {
        return new TopicExchange(NotificationEventConstants.EXCHANGE_ROOM);
    }

    @Bean
    public Queue notificationBillQueue() {
        return QueueBuilder.durable(NotificationEventConstants.QUEUE_NOTIFICATION_BILL).build();
    }

    @Bean
    public Binding notificationBillBinding() {
        return BindingBuilder
                .bind(notificationBillQueue())
                .to(roomExchangeForNotification())
                .with(NotificationEventConstants.RK_BILL_NOTIFICATION);
    }
}
