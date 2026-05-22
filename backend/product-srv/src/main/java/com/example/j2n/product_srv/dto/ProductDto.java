package com.example.j2n.product_srv.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class ProductDto {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Builder.Default
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock = 0;

    private String thumbnail;

    @Size(max = 100, message = "Duration must not exceed 100 characters")
    private String duration;

    @Size(max = 255, message = "Start location must not exceed 255 characters")
    private String startLocation;

    @Size(max = 50, message = "Size must not exceed 50 characters")
    private String size;

    @Size(max = 255, message = "Design must not exceed 255 characters")
    private String design;

    @NotBlank(message = "Type is required")
    private String type;
}
