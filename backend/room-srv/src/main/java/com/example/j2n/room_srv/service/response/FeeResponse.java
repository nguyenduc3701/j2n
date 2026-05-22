package com.example.j2n.room_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Data transfer object for fee configuration information")
public class FeeResponse {

    @Schema(description = "ID of the fee", example = "1")
    private Long id;

    @Schema(description = "Name of the fee", example = "Water")
    private String name;

    @Schema(description = "Unit price of the fee", example = "100000.0")
    private BigDecimal unitPrice;

    @Schema(description = "Unit name of the fee", example = "Person")
    private String unitName;

    @Schema(description = "Active status of the fee", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Update timestamp")
    private LocalDateTime updatedAt;
}
