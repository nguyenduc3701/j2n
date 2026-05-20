package com.example.j2n.bff_srv.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateFeeRequest {

    @NotBlank(message = "Fee name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "Fee name", example = "Electricity", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be at least 0")
    @Schema(description = "Unit price of the fee", example = "3500.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    @NotBlank(message = "Unit name is required")
    @Size(max = 50, message = "Unit name must not exceed 50 characters")
    @Schema(description = "Unit of measurement (e.g., kWh, m3, month)", example = "kWh", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unitName;
}
