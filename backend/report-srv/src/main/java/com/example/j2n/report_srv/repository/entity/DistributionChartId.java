package com.example.j2n.report_srv.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionChartId implements Serializable {
    private String chartType;
    private String itemLabel;
}
