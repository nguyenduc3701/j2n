package com.example.j2n.travel_srv.repository;

import com.example.j2n.travel_srv.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
    List<TourEntity> findByCategoryId(Long categoryId);
}
