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
@Table(name = "room_utility_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUtilityReport {

    @Id
    @Column(name = "month_year", length = 7)
    private String monthYear;

    @Column(name = "total_electricity")
    private BigDecimal totalElectricity;

    @Column(name = "total_water")
    private BigDecimal totalWater;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
