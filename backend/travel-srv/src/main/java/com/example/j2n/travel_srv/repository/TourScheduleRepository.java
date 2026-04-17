package com.example.j2n.travel_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.travel_srv.repository.entity.TourScheduleEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourScheduleEntity, Long> {
    List<TourScheduleEntity> findAllByTourIdAndIsDeletedFalseOrderByDayNumberAsc(Long tourId);
    Optional<TourScheduleEntity> findByIdAndIsDeletedFalse(Long id);
}
