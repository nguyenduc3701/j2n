package com.example.j2n.travel_srv.repository;

import com.example.j2n.travel_srv.entity.TourScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourScheduleEntity, Long> {
    List<TourScheduleEntity> findByTourIdOrderByDayNumberAsc(Long tourId);
}
