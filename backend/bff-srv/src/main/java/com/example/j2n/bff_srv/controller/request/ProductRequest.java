package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotNull(message = "Category ID is required")
    @JsonProperty("categoryId")
    @JsonAlias("category_id")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private String thumbnail;

    @Size(max = 100, message = "Duration must not exceed 100 characters")
    private String duration;

    @Size(max = 255, message = "Start location must not exceed 255 characters")
    @JsonProperty("startLocation")
    @JsonAlias("start_location")
    private String startLocation;

    @NotBlank(message = "Type is required")
    private String type;
}
