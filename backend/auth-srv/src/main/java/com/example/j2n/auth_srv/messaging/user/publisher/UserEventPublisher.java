package com.example.j2n.auth_srv.messaging.user.publisher;

import com.example.j2n.auth_srv.messaging.user.constant.UserEventConstants;
import com.example.j2n.messaging.publisher.BaseEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.j2n.auth_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.auth_srv.messaging.user.event.UserUpdatedEvent;
import com.example.j2n.auth_srv.messaging.user.event.UserDeletedEvent;

@Component
public class UserEventPublisher extends BaseEventPublisher {

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishUserRegistered(UserRegisteredEvent event) {
        publish(UserEventConstants.EXCHANGE_USER, UserEventConstants.RK_USER_REGISTERED, event);
    }

    public void publishUserUpdated(UserUpdatedEvent event) {
        publish(UserEventConstants.EXCHANGE_USER, UserEventConstants.RK_USER_UPDATED, event);
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        publish(UserEventConstants.EXCHANGE_USER, UserEventConstants.RK_USER_DELETED, event);
    }
}
