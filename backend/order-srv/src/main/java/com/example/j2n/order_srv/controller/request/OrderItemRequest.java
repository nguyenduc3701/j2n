package com.example.j2n.order_srv.controller.request;

import com.example.j2n.dto.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for adding or updating an item in the order")
public class OrderItemRequest extends BaseRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "The unique identifier of the user", example = "user-123")
    private String userId;

    @NotBlank(message = "Item ID is required")
    @Schema(description = "The unique identifier of the item (e.g., Product ID)", example = "product-789")
    private String itemId;

    @NotBlank(message = "Item type is required")
    @Schema(description = "The category of the item", example = "PRODUCT")
    private String itemType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "The number of items to add", example = "2")
    private Integer quantity;

    @Schema(description = "Additional JSON metadata specific to the item type", example = "{\"productDate\": \"2024-12-25\", \"pax\": 2}")
    private Map<String, Object> metadata;
}
