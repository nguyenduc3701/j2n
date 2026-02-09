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
@Table(name = "global_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatistics {

    @Id
    private Integer id = 1;

    @Column(name = "total_users")
    private Long totalUsers;

    @Column(name = "total_rooms")
    private Long totalRooms;

    @Column(name = "total_revenue_all_time")
    private BigDecimal totalRevenueAllTime;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
