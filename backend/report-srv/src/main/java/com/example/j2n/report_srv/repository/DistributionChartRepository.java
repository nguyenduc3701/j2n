package com.example.j2n.report_srv.repository;

import com.example.j2n.report_srv.repository.entity.DistributionChart;
import com.example.j2n.report_srv.repository.entity.DistributionChartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistributionChartRepository extends JpaRepository<DistributionChart, DistributionChartId> {
    Optional<DistributionChart> findByChartTypeAndItemLabel(String chartType, String itemLabel);
}
