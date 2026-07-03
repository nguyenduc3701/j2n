package com.example.j2n.notification_srv.messaging.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BillNotificationEvent {

    private String billId;
    private String roomNumber;
    private Integer billingMonth;
    private BigDecimal electricAmount;
    private BigDecimal waterAmount;
    private BigDecimal roomAmount;
    private BigDecimal serviceAmount;
    private BigDecimal totalAmount;
    private Integer electricityUsage;
    private String qrPaymentUrl;
    private List<MemberInfo> members;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MemberInfo {
        private String userId;
        private String fullName;
        private String email;
    }
}
