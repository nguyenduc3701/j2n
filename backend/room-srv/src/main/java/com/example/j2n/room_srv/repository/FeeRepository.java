package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.FeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<FeeEntity, Long> {
    List<FeeEntity> findByIsActiveTrue();
}
