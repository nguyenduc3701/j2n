package com.example.j2n.order_srv.messaging.payment.constant;

public class PaymentEventConstants {
    public static final String EXCHANGE_PAYMENT = "payment.exchange";

    // Queue this service subscribes to
    public static final String QUEUE_ORDER_PAYMENT_CONFIRMED = "order.payment.confirmed.queue";

    // Routing keys
    public static final String RK_PAYMENT_CONFIRMED = "payment.transaction.confirmed";
}
