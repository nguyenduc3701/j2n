package com.example.j2n.product_srv.messaging.product.constant;

import lombok.Data;

@Data
public class ProductEventConstants {
    public static final String EXCHANGE_PRODUCT = "product.exchange";
    // Queue
    public static final String QUEUE_PRODUCT_IMAGE = "product.product_image.queue";
    // Routing key
    public static final String RK_PRODUCT_IMAGE_UPLOADED = "product.product_image.uploaded";
    public static final String RK_PRODUCT_CREATED = "product.product.created";
    public static final String RK_PRODUCT_UPDATED = "product.product.updated";
    public static final String RK_PRODUCT_DELETED = "product.product.deleted";

    public static final String RK_PRODUCT_STOCK_LOCK = "payment.reservation.lock";
    public static final String RK_PRODUCT_STOCK_RELEASE = "payment.reservation.release";
    public static final String RK_PRODUCT_STOCK_CONFIRM = "payment.reservation.confirm";

    // Reservation Queues
    public static final String QUEUE_PRODUCT_STOCK_LOCK = "payment.reservation.lock.queue";
    public static final String QUEUE_PRODUCT_STOCK_RELEASE = "payment.reservation.release.queue";
    public static final String QUEUE_PRODUCT_STOCK_CONFIRM = "payment.reservation.confirm.queue";
}
