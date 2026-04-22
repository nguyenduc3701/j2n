package com.example.j2n.order_srv.dto.response;

import com.example.j2n.order_srv.repository.entity.CartItemEntity;
import com.example.j2n.order_srv.repository.entity.ProductInfoEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemWithProductResponse {
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

    public static CartItemWithProductResponse from(CartItemEntity item, ProductInfoEntity productInfo) {
        CartItemWithProductResponse.ProductInfoResponse productResponse = null;
        if (productInfo != null) {
            productResponse = CartItemWithProductResponse.ProductInfoResponse.builder()
                    .id(productInfo.getId())
                    .title(productInfo.getTitle())
                    .price(productInfo.getPrice())
                    .thumbnail(productInfo.getThumbnail())
                    .build();
        }

        return CartItemWithProductResponse.builder()
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
