package com.example.j2n.report_srv.messaging.payment.consumer;

import com.example.j2n.report_srv.messaging.payment.constant.PaymentEventConstants;
import com.example.j2n.report_srv.messaging.payment.event.PaymentConfirmedEvent;
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
public class PaymentEventConsumer {

    private final ManagementService managementService;

    @RabbitListener(queues = PaymentEventConstants.QUEUE_REPORT_PAYMENT_CONFIRMED)
    public void onPaymentConfirmed(
            PaymentConfirmedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[REPORT-SRV][EVENT] payment.confirmed received txId={} userId={} amount={}",
                    event.getTransactionId(), event.getUserId(), event.getAmount());
            managementService.handlePaymentConfirmed(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[REPORT-SRV][EVENT] Failed to process payment.confirmed event: {}", e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
