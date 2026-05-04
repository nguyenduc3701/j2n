package com.example.j2n.report_srv.repository;

import com.example.j2n.report_srv.repository.entity.MonthlyFinancials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyFinancialsRepository extends JpaRepository<MonthlyFinancials, Integer> {
    java.util.Optional<MonthlyFinancials> findByMonthYearAndDomain(String monthYear, String domain);
}
