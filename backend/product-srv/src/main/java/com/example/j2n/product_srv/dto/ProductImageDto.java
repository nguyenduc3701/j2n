package com.example.j2n.product_srv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long productId;
    private String imageUrl;
    private Boolean isPrimary;
}
