package com.example.j2n.payment_srv.messaging.reservation.publisher;

import com.example.j2n.messaging.publisher.BaseEventPublisher;
import com.example.j2n.payment_srv.messaging.reservation.constant.ReservationConstants;
import com.example.j2n.payment_srv.messaging.reservation.event.StockReservationEvent;
import com.example.j2n.payment_srv.messaging.product.constant.ProductEventConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationPublisher extends BaseEventPublisher {

    public ReservationPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    public void publishReservationDelay(StockReservationEvent event) {
        publish(ReservationConstants.EXCHANGE_RESERVATION, ReservationConstants.RK_RESERVATION_DELAY, event);
    }

    public void publishLockStock(StockReservationEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ReservationConstants.RK_STOCK_LOCK, event);
    }

    public void publishReleaseStock(StockReservationEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ReservationConstants.RK_STOCK_RELEASE, event);
    }

    public void publishConfirmStock(StockReservationEvent event) {
        publish(ProductEventConstants.EXCHANGE_PRODUCT, ReservationConstants.RK_STOCK_CONFIRM, event);
    }
}
