package com.example.j2n.order_srv.messaging.payment.consumer;

import com.example.j2n.order_srv.messaging.payment.constant.PaymentEventConstants;
import com.example.j2n.order_srv.messaging.payment.event.PaymentConfirmedEvent;
import com.example.j2n.order_srv.service.OrderService;
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

    private final OrderService orderService;

    /**
     * When a payment is confirmed, delete all order items for the paying user
     * via OrderService.
     */
    @RabbitListener(queues = PaymentEventConstants.QUEUE_ORDER_PAYMENT_CONFIRMED)
    public void onPaymentConfirmed(
            PaymentConfirmedEvent event,
            Channel channel,
            Message message) throws IOException {
        try {
            log.info("[ORDER-SRV][EVENT] payment.confirmed received userId={} txId={}",
                    event.getUserId(), event.getTransactionId());
            orderService.clearCartAfterPayment(event.getUserId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ORDER-SRV][EVENT] Failed to process payment.confirmed event: {}", e.getMessage(), e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
