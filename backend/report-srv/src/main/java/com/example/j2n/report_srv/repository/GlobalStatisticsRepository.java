package com.example.j2n.report_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.report_srv.repository.entity.GlobalStatistics;

@Repository
public interface GlobalStatisticsRepository extends JpaRepository<GlobalStatistics, Integer> {
}
