package com.example.j2n.product_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.product_srv.repository.entity.ProductImageEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findAllByProductIdAndIsDeletedFalse(Long productId);
    Optional<ProductImageEntity> findByIdAndIsDeletedFalse(Long id);
    List<ProductImageEntity> findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(Long productId);
    boolean existsByProductIdAndImageUrlAndIsDeletedFalse(Long productId, String imageUrl);
}
