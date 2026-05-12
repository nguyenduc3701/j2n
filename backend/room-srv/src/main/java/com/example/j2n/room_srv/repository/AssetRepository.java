package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Long>, JpaSpecificationExecutor<AssetEntity> {
    Optional<AssetEntity> findByName(String name);
}
