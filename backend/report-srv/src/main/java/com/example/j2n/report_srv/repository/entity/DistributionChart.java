package com.example.j2n.report_srv.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Using IdClass for composite key
@Entity
@Table(name = "distribution_charts")
@IdClass(DistributionChartId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionChart {

    @Id
    @Column(name = "chart_type", length = 50)
    private String chartType;

    @Id
    @Column(name = "item_label", length = 100)
    private String itemLabel;

    @Column(name = "item_value")
    private Long itemValue;
}
