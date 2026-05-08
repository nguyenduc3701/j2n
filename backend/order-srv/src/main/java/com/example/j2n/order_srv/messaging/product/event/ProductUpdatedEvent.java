package com.example.j2n.order_srv.messaging.product.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductUpdatedEvent {
    private String productId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String thumbnail;
    private String duration;
    private String startLocation;
    private String size;
    private String design;
    private String type;
    private Boolean isDeleted;
    private String updatedAt;
}
