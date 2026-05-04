package com.example.j2n.payment_srv.messaging.payment.publisher;

import com.example.j2n.messaging.publisher.BaseEventPublisher;
import com.example.j2n.payment_srv.messaging.payment.constant.PaymentEventConstants;
import com.example.j2n.payment_srv.messaging.payment.event.PaymentConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventPublisher extends BaseEventPublisher {

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishPaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("[PAYMENT-SRV] Publishing payment.confirmed for txId={}", event.getTransactionId());
        publish(PaymentEventConstants.EXCHANGE_PAYMENT, PaymentEventConstants.RK_PAYMENT_CONFIRMED, event);
    }
}
