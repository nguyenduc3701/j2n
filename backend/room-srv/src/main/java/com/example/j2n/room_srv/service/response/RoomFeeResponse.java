package com.example.j2n.room_srv.service.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoomFeeResponse {
    @Schema(description = "Room fee assignment ID", example = "1")
    private Long id;

    @Schema(description = "Fee ID", example = "1")
    private Long feeId;

    @Schema(description = "Fee name", example = "Electricity")
    private String name;

    @Schema(description = "Unit price", example = "3500.0")
    private BigDecimal unitPrice;

    @Schema(description = "Unit name", example = "kWh")
    private String unitName;

}
