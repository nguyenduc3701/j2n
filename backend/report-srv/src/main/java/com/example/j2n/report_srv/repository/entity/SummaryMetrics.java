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

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryMetrics {

    @Id
    @Column(name = "metric_key", length = 100)
    private String metricKey;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "metric_value")
    private Long metricValue;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
