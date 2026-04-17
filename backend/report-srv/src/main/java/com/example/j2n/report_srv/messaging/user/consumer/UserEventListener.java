package com.example.j2n.report_srv.messaging.user.consumer;

import com.example.j2n.report_srv.messaging.user.constant.UserEventConstants;
import com.example.j2n.report_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.report_srv.service.ManagementService;
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

    private final ManagementService managementService;

    @RabbitListener(queues = UserEventConstants.QUEUE_REPORT_USER)
    public void onUserRegistered(
            UserRegisteredEvent event,
            Channel channel,
            Message message) throws IOException {

        try {
            log.info("[REPORT-SRV][EVENT] user.registered userId={}", event.getUserId());
            managementService.handleUserRegistrationReport(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("[REPORT-SRV][EVENT] user.registered processed successfully for userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process user registration event: {}. Error: {}", event.getUserId(),
                    e.getMessage());
            channel.basicNack(
                    message.getMessageProperties().getDeliveryTag(),
                    false,
                    false // false = send to DLQ (sau này)
            );
        }

    }
}
