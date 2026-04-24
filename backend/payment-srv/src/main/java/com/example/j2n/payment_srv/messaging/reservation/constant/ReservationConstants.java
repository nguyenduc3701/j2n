package com.example.j2n.payment_srv.messaging.reservation.constant;

public class ReservationConstants {
    public static final String EXCHANGE_RESERVATION = "payment.reservation.exchange";
    
    // Delays for stock release
    public static final String QUEUE_RESERVATION_DELAY = "payment.reservation.delay.queue";
    public static final String QUEUE_RESERVATION_RELEASE = "payment.reservation.release.queue";
    
    public static final String RK_RESERVATION_DELAY = "payment.reservation.delay";
    public static final String RK_RESERVATION_RELEASE = "payment.reservation.release";
    
    // Commands to product-srv
    public static final String RK_STOCK_LOCK = "payment.reservation.lock";
    public static final String RK_STOCK_RELEASE = "payment.reservation.release";
    public static final String RK_STOCK_CONFIRM = "payment.reservation.confirm";
}
