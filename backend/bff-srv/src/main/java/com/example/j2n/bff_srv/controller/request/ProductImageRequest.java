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
public class ProductImageRequest {
    @NotNull(message = "Product ID is required")
    @JsonProperty("productId")
    @JsonAlias("product_id")
    private Long productId;

    @NotBlank(message = "Image URL is required")
    @JsonProperty("imageUrl")
    @JsonAlias("image_url")
    private String imageUrl;

    @JsonProperty("isPrimary")
    @JsonAlias("is_primary")
    private Boolean isPrimary;
}
