package com.example.j2n.report_srv.messaging.user.consumer;

import com.example.j2n.report_srv.messaging.user.constant.UserEventConstants;
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
    @RabbitListener(queues = UserEventConstants.QUEUE_AUTH_AVATAR)
    public void onAvatarUploaded(
            Object event,
            Channel channel,
            Message message) throws IOException {

        log.info("[EVENT] user.avatar.uploaded");

    }
}
