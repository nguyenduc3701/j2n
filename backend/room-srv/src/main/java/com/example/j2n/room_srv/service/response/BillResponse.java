package com.example.j2n.room_srv.service.response;

import com.example.j2n.room_srv.constant.BillStatus;
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
public class BillResponse {

    @Schema(description = "Bill ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private String id;

    @Schema(description = "Room ID", example = "1")
    private Long roomId;

    @Schema(description = "Room number", example = "101")
    private String roomNumber;

    @Schema(description = "Billing month", example = "5")
    private Integer billingMonth;

    @Schema(description = "Old electricity meter index", example = "100")
    private Integer electricityOldIndex;

    @Schema(description = "New electricity meter index", example = "150")
    private Integer electricityNewIndex;

    @Schema(description = "Electricity usage", example = "50")
    private Integer electricityUsage;

    @Schema(description = "Water usage", example = "10")
    private Integer waterUsage;

    @Schema(description = "Electricity charges", example = "175000")
    private BigDecimal electricAmount;

    @Schema(description = "Water charges", example = "30000")
    private BigDecimal waterAmount;

    @Schema(description = "Room base rent", example = "2000000")
    private BigDecimal roomAmount;

    @Schema(description = "Other fees amount", example = "50000")
    private BigDecimal otherFeesAmount;

    @Schema(description = "Additional service fees", example = "50000")
    private BigDecimal serviceFees;

    @Schema(description = "Total bill amount", example = "2500000")
    private BigDecimal totalAmount;

    @Schema(description = "Bill status", example = "UNPAID")
    private BillStatus status;

    @Schema(description = "Order ID associated with payment", example = "1001")
    private Long orderId;

    @Schema(description = "Creation timestamp", example = "2024-05-11T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-05-11T10:00:00")
    private LocalDateTime updatedAt;
}
