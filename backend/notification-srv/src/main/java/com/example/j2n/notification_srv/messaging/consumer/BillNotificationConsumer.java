package com.example.j2n.notification_srv.messaging.consumer;

import com.example.j2n.notification_srv.messaging.constant.NotificationEventConstants;
import com.example.j2n.notification_srv.messaging.event.BillNotificationEvent;
import com.example.j2n.notification_srv.service.EmailService;
import com.example.j2n.notification_srv.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillNotificationConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @RabbitListener(queues = NotificationEventConstants.QUEUE_NOTIFICATION_BILL)
    public void handleBillNotification(BillNotificationEvent event,
                                       Channel channel,
                                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("[NOTIFICATION-SRV] Received bill notification event for room: {}, month: {}",
                    event.getRoomNumber(), event.getBillingMonth());

            if (event.getMembers() == null || event.getMembers().isEmpty()) {
                log.warn("[NOTIFICATION-SRV] No members to notify for room: {}", event.getRoomNumber());
                channel.basicAck(deliveryTag, false);
                return;
            }

            for (BillNotificationEvent.MemberInfo member : event.getMembers()) {
                try {
                    // Send email to each member
                    if (member.getEmail() != null && !member.getEmail().isBlank()) {
                        emailService.sendBillEmail(member, event);
                        log.info("[NOTIFICATION-SRV] Email sent successfully to: {}", member.getEmail());
                    }

                    // Save in-app notification for each member
                    notificationService.createBillNotification(member.getUserId(), event);
                    log.info("[NOTIFICATION-SRV] In-app notification created for user: {}", member.getUserId());
                } catch (Exception e) {
                    log.error("[NOTIFICATION-SRV] Failed to process notification for member: {}, error: {}",
                            member.getUserId(), e.getMessage(), e);
                }
            }

            channel.basicAck(deliveryTag, false);
            log.info("[NOTIFICATION-SRV] Bill notification processed successfully for room: {}", event.getRoomNumber());
        } catch (Exception e) {
            log.error("[NOTIFICATION-SRV] Failed to process bill notification event: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception nackEx) {
                log.error("[NOTIFICATION-SRV] Failed to NACK message: {}", nackEx.getMessage());
            }
        }
    }
}
