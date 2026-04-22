package com.example.j2n.product_srv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductScheduleDto {
    private Long productId;
    private Integer dayNumber;
    private String title;
    private String content;
}
