package com.example.j2n.auth_srv.messaging.user.consumer;

import com.example.j2n.auth_srv.messaging.user.constant.UserEventConstants;
import com.example.j2n.auth_srv.messaging.user.event.UserAvatarUploadEvent;
import com.example.j2n.auth_srv.service.UserService;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @RabbitListener(queues = UserEventConstants.QUEUE_AUTH_AVATAR)
    public void onAvatarUploaded(
            UserAvatarUploadEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[AUTH-SRV][EVENT] user.avatar.uploaded userId={}, imageUrl={}",
                    event.getUserId(), event.getImageUrl());
            userService.updateUserImageUrl(event.getUserId(), event.getImageUrl());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("[AUTH-SRV][EVENT] user.avatar.uploaded userId={}, imageUrl={}",
                    event.getUserId(), event.getImageUrl());
        } catch (Exception e) {
            log.error("[AUTH-SRV][EVENT] Failed to process avatar upload", e);
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false // false = send to DLQ (sau này)
            );
        }
    }
}
