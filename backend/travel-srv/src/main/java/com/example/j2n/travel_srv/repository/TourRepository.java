package com.example.j2n.travel_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.travel_srv.repository.entity.TourEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
    List<TourEntity> findAllByIsDeletedFalse();
    Optional<TourEntity> findByIdAndIsDeletedFalse(Long id);
    List<TourEntity> findByCategoryIdAndIsDeletedFalse(Long categoryId);
}
