package com.example.j2n.image_srv.repository;

import com.example.j2n.image_srv.repository.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findByOwnerTypeAndOwnerIdAndIsActiveTrue(String ownerType, Long ownerId);
}
