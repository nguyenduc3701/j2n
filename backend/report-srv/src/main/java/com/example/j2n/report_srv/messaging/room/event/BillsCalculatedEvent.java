package com.example.j2n.report_srv.messaging.room.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillsCalculatedEvent {
    private Integer totalBills;
    private BigDecimal totalUnpaidAmount;
    private BigDecimal totalElectricityAmount;
    private BigDecimal totalWaterAmount;
    private String monthYear;
}
