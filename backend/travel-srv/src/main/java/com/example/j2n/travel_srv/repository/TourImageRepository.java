package com.example.j2n.travel_srv.repository;

import com.example.j2n.travel_srv.entity.TourImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImageEntity, Long> {
    List<TourImageEntity> findByTourId(Long tourId);
}
