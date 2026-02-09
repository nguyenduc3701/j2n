package com.example.j2n.report_srv.repository;

import com.example.j2n.report_srv.repository.entity.ActivePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivePromotionRepository extends JpaRepository<ActivePromotion, String> {
}
