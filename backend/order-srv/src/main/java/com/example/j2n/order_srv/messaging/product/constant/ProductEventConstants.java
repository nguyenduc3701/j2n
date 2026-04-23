package com.example.j2n.order_srv.messaging.product.constant;

public class ProductEventConstants {
    public static final String EXCHANGE_PRODUCT = "product.exchange";
    
    public static final String QUEUE_ORDER_PRODUCT_SYNC = "order.product.sync.queue";
    
    public static final String RK_PRODUCT_CREATED = "product.product.created";
    public static final String RK_PRODUCT_UPDATED = "product.product.updated";
    public static final String RK_PRODUCT_DELETED = "product.product.deleted";
    public static final String RK_PRODUCT_ALL = "product.product.#";
}
