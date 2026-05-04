package com.example.j2n.report_srv.messaging.payment.constant;

public class PaymentEventConstants {
    public static final String EXCHANGE_PAYMENT = "payment.exchange";

    // Queue this service subscribes to
    public static final String QUEUE_REPORT_PAYMENT_CONFIRMED = "report.payment.confirmed.queue";

    // Routing keys
    public static final String RK_PAYMENT_CONFIRMED = "payment.transaction.confirmed";
}
