package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomFeeDto {
    @Schema(description = "ID of the fee", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Fee ID is required")
    private Long feeId;

}
