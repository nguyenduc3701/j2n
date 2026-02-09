package com.example.j2n.report_srv.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyMetrics {

    @Id
    @Column(name = "month_year", length = 7)
    private String monthYear;

    @Column(name = "new_users")
    private Integer newUsers;

    @Column(name = "monthly_revenue")
    private BigDecimal monthlyRevenue;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
