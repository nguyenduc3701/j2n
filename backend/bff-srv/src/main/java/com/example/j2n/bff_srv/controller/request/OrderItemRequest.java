package com.example.j2n.bff_srv.controller.request;

import com.example.j2n.dto.BaseRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request object for adding or updating an item in the order")
public class OrderItemRequest extends BaseRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "The unique identifier of the user", example = "user-123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotBlank(message = "Item ID is required")
    @Schema(description = "The unique identifier of the item (e.g., Product ID)", example = "product-789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemId;

    @NotBlank(message = "Item type is required")
    @Schema(description = "The category of the item", example = "PRODUCT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "The number of items to add", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "The size of the item", example = "M", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String size;

    @Schema(description = "The design of the item", example = "Classic", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String design;

    @Schema(description = "Additional JSON metadata specific to the item type", example = "{\"productDate\": \"2024-12-25\", \"pax\": 2}", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> metadata;
}
