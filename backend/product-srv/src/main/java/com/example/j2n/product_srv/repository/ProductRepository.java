package com.example.j2n.product_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.product_srv.repository.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByIsDeletedFalse();
    Optional<ProductEntity> findByIdAndIsDeletedFalse(Long id);
    List<ProductEntity> findByCategoryIdAndIsDeletedFalse(Long categoryId);
}
