package com.example.j2n.payment_srv.service.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.j2n.payment_srv.repository.entity.TransactionEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private TransactionEntity.Status status;
    private TransactionEntity.PaymentMethod paymentMethod;
    private String externalTxId;
    private String description;
    private String checkoutUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse fromEntity(TransactionEntity entity, String checkoutUrl) {
        return TransactionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .externalTxId(entity.getExternalTxId())
                .description(entity.getDescription())
                .checkoutUrl(checkoutUrl)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
