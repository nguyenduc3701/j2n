package com.example.j2n.bff_srv.controller.request;

import com.example.j2n.validation.ValidNotEmpty;
import com.example.j2n.validation.ValidatableRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ValidNotEmpty
public class UpdateFeeRequest implements ValidatableRequest {

    @Schema(description = "Fee name", example = "Electricity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Builder.Default
    private Optional<String> name = Optional.empty();

    @Schema(description = "Unit price of the fee", example = "3500.0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @DecimalMin(value = "0.0", message = "Unit price must be at least 0")
    @Builder.Default
    private Optional<BigDecimal> unitPrice = Optional.empty();

    @Schema(description = "Unit of measurement (e.g., kWh, m3, month)", example = "kWh", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "Unit name must not exceed 50 characters")
    @Builder.Default
    private Optional<String> unitName = Optional.empty();

    @Schema(description = "Active status", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Builder.Default
    private Optional<Boolean> isActive = Optional.empty();

    @Override
    public boolean isEmpty() {
        return name.isEmpty() && unitPrice.isEmpty() && unitName.isEmpty() && isActive.isEmpty();
    }
}
