package com.example.j2n.payment_srv.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
public class CartItemRequest {

    @Schema(description = "User ID", example = "user-123")
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Item ID (Tour ID, etc.)", example = "tour-456")
    @NotBlank(message = "Item ID is required")
    private String itemId;

    @Schema(description = "Item Type", example = "TOUR")
    @NotBlank(message = "Item Type is required")
    private String itemType;

    @Schema(description = "Quantity", example = "1")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Metadata (JSON string)", example = "{\"tourDate\": \"2024-05-01\"}")
    private String metadata;
}
