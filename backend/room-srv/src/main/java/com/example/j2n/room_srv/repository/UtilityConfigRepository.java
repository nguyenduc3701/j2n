package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityConfigRepository extends JpaRepository<UtilityConfigEntity, Long> {
    List<UtilityConfigEntity> findByIsActiveTrue();
}
