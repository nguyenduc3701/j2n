package com.example.j2n.travel_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.travel_srv.repository.entity.TourImageEntity;

import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImageEntity, Long> {
    List<TourImageEntity> findByTourId(Long tourId);
}
