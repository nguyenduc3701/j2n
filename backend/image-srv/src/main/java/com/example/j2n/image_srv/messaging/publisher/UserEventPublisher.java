package com.example.j2n.image_srv.messaging.publisher;

import com.example.j2n.messaging.constant.UserEventConstants;
import com.example.j2n.messaging.event.UserAvatarUploadEvent;
import com.example.j2n.messaging.publisher.BaseEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class UserEventPublisher extends BaseEventPublisher {

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishAvatarUploaded(UserAvatarUploadEvent event) {
        publish(UserEventConstants.EXCHANGE_USER, UserEventConstants.RK_AVATAR_UPLOADED, event);
    }

}
