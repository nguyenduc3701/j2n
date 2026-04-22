package com.example.j2n.order_srv.service.response;

import com.example.j2n.order_srv.repository.entity.OrderItemEntity;
import com.example.j2n.order_srv.repository.entity.ProductInfoEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemWithProductResponse {
    private Long id;
    private String userId;
    private String itemId;
    private String itemType;
    private Integer quantity;
    private Object metadata;
    private ProductInfoResponse product;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfoResponse {
        private Long id;
        private String title;
        private java.math.BigDecimal price;
        private String thumbnail;
    }

    public static OrderItemWithProductResponse from(OrderItemEntity item, ProductInfoEntity productInfo) {
        OrderItemWithProductResponse.ProductInfoResponse productResponse = null;
        if (productInfo != null) {
            productResponse = OrderItemWithProductResponse.ProductInfoResponse.builder()
                    .id(productInfo.getId())
                    .title(productInfo.getTitle())
                    .price(productInfo.getPrice())
                    .thumbnail(productInfo.getThumbnail())
                    .build();
        }

        return OrderItemWithProductResponse.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .itemId(item.getItemId())
                .itemType(item.getItemType())
                .quantity(item.getQuantity())
                .metadata(item.getMetadata())
                .product(productResponse)
                .build();
    }
}
