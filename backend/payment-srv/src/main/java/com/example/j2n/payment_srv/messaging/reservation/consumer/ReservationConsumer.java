package com.example.j2n.payment_srv.messaging.reservation.consumer;

import com.example.j2n.payment_srv.messaging.reservation.constant.ReservationConstants;
import com.example.j2n.payment_srv.messaging.reservation.event.StockReservationEvent;
import com.example.j2n.payment_srv.messaging.reservation.publisher.ReservationPublisher;
import com.example.j2n.payment_srv.service.ProductInfoService;
import com.example.j2n.payment_srv.service.TransactionService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationConsumer {

    private final TransactionService transactionService;
    private final ProductInfoService productInfoService;
    private final ReservationPublisher reservationPublisher;

    @RabbitListener(queues = ReservationConstants.QUEUE_RESERVATION_RELEASE)
    @Transactional
    public void handleReservationRelease(StockReservationEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[PAYMENT-SRV] Reservation release received for transaction: {}", event.getTransactionId());
            
            boolean cancelled = transactionService.cancelPendingTransaction(event.getTransactionId());
            
            if (cancelled) {
                log.info("[PAYMENT-SRV] Transaction {} still PENDING, releasing stock", event.getTransactionId());
                // Release locally
                productInfoService.releaseStock(event);
                // Inform product-srv
                reservationPublisher.publishReleaseStock(event);
            } else {
                log.info("[PAYMENT-SRV] Transaction {} not in PENDING status, no release needed", event.getTransactionId());
            }
            
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[PAYMENT-SRV] Failed to handle reservation release", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
