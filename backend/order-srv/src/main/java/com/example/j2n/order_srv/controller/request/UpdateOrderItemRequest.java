package com.example.j2n.order_srv.controller.request;

import com.example.j2n.dto.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating an item quantity in the order")
public class UpdateOrderItemRequest extends BaseRequest {

    @NotBlank(message = "Item ID is required")
    @Schema(description = "The unique identifier of the item", example = "product-789")
    private String itemId;

    @NotBlank(message = "Item type is required")
    @Schema(description = "The category of the item", example = "PRODUCT")
    private String itemType;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be at least 0")
    @Schema(description = "The target number of items (setting to 0 will remove the item)", example = "2")
    private Integer quantity;
}
