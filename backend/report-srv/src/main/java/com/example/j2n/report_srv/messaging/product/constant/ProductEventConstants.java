package com.example.j2n.report_srv.messaging.product.constant;

import lombok.Data;

@Data
public class ProductEventConstants {
    public static final String EXCHANGE_PRODUCT = "product.exchange";
    // Queue
    public static final String QUEUE_REPORT_PRODUCT_TOUR = "report.product.product.queue";
    // Routing key
    public static final String RK_PRODUCT_CREATED = "product.product.created";
    public static final String RK_PRODUCT_DELETED = "product.product.deleted";
    public static final String RK_PRODUCT_ALL = "product.#";
}
