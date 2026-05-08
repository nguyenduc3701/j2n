package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductScheduleRequest {
    @NotNull(message = "Product ID is required")
    @JsonProperty("productId")
    @JsonAlias("product_id")
    private Long productId;

    @NotNull(message = "Day number is required")
    @JsonProperty("dayNumber")
    @JsonAlias("day_number")
    private Integer dayNumber;

    @NotBlank(message = "Title is required")
    private String title;

    private String content;
    private String hotel;
    private String breakfast;
    private String lunch;
    private String dinner;
}
