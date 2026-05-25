package com.example.j2n.room_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateRoomAssetRequest {

    @Schema(description = "List of assets mapping with quantity", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Assets list cannot be null")
    @Valid
    private List<AssetMapping> assets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AssetMapping {
        @Schema(description = "Asset ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Asset ID cannot be null")
        private Long assetId;

        @Schema(description = "Quantity of the asset mapped to the room", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
