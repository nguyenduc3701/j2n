package com.example.j2n.report_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.report_srv.repository.entity.MonthlyMetrics;

@Repository
public interface MonthlyMetricsRepository extends JpaRepository<MonthlyMetrics, String> {
}
