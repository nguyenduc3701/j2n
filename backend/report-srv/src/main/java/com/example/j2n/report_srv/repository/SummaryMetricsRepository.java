package com.example.j2n.report_srv.repository;

import com.example.j2n.report_srv.repository.entity.SummaryMetrics;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryMetricsRepository extends JpaRepository<SummaryMetrics, String> {
    @Modifying
    @Query("UPDATE SummaryMetrics s SET s.metricValue = s.metricValue + 1 WHERE s.metricKey = :key")
    int incrementValue(@Param("key") String key);

    @Modifying
    @Query("UPDATE SummaryMetrics s SET s.metricValue = s.metricValue - 1 WHERE s.metricKey = :key AND s.metricValue > 0")
    int decrementValueIfGreaterThanZero(@Param("key") String key);

    @Modifying
    @Query("UPDATE SummaryMetrics s SET s.metricValue = :value WHERE s.metricKey = :key")
    int updateValue(@Param("key") String key, @Param("value") Long value);
}
