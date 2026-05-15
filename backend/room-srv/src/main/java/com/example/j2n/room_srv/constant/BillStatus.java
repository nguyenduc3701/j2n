package com.example.j2n.room_srv.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BillStatus {
    UNPAID("UNPAID"),
    PAID("PAID"),
    PARTIAL("PARTIAL"),
    CANCELLED("CANCELLED");

    private final String value;
}
