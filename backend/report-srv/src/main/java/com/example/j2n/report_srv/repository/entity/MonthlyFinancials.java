package com.example.j2n.report_srv.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "monthly_financials", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "month_year", "domain" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFinancials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "month_year", length = 7)
    private String monthYear;

    @Column(name = "domain", length = 20)
    private String domain;

    @Column(name = "total_income")
    private BigDecimal totalIncome;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;
}
