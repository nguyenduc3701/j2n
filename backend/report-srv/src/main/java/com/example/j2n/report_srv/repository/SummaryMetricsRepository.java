package com.example.j2n.report_srv.repository;

import com.example.j2n.report_srv.repository.entity.SummaryMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryMetricsRepository extends JpaRepository<SummaryMetrics, String> {
}
