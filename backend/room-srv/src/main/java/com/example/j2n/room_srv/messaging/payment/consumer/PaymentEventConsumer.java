package com.example.j2n.room_srv.messaging.payment.consumer;

import com.example.j2n.room_srv.messaging.payment.constant.PaymentEventConstants;
import com.example.j2n.room_srv.messaging.payment.event.PaymentConfirmedEvent;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
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
public class PaymentEventConsumer {

    private final BillRepository billRepository;
    private final com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher roomEventPublisher;

    @RabbitListener(queues = PaymentEventConstants.QUEUE_ROOM_PAYMENT_CONFIRMED)
    @Transactional
    public void handlePaymentConfirmed(PaymentConfirmedEvent event, Channel channel, Message message) throws IOException {
        try {
            log.info("[ROOM-SRV] Processing payment confirmation for user: {}", event.getUserId());
            
            for (PaymentConfirmedEvent.OrderItem item : event.getItems()) {
                billRepository.findById(item.getProductId()).ifPresent(bill -> {
                    log.info("[ROOM-SRV] Updating bill {} to PAID", bill.getId());
                    bill.setStatus(com.example.j2n.room_srv.enums.BillStatus.PAID);
                    billRepository.save(bill);

                    // Report revenue
                    roomEventPublisher.publishRoomRevenue(com.example.j2n.room_srv.messaging.report.event.RoomRevenueEvent.builder()
                            .billId(bill.getId())
                            .roomNumber(bill.getRoom().getRoomNumber())
                            .amount(bill.getTotalAmount())
                            .paidAt(java.time.LocalDateTime.now())
                            .build());
                });
            }
            
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("[ROOM-SRV] Failed to process payment confirmation", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
