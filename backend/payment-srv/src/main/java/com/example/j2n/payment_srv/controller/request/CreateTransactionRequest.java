package com.example.j2n.payment_srv.controller.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransactionRequest {
    @NotNull
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @NotNull
    @JsonProperty("userId")
    private String userId;

    @NotNull
    @JsonProperty("orderIds")
    private List<Long> orderIds;
}
