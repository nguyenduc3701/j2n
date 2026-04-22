package com.example.j2n.image_srv.messaging.product.constant;

import lombok.Data;

@Data
public class ProductEventConstants {
    public static final String EXCHANGE_PRODUCT = "product.exchange";
    public static final String RK_TOUR_IMAGE_UPLOADED = "product.product_image.uploaded";
}
