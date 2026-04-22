package com.example.j2n.product_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.product_srv.repository.entity.ProductScheduleEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductScheduleRepository extends JpaRepository<ProductScheduleEntity, Long> {
    List<ProductScheduleEntity> findAllByProductIdAndIsDeletedFalseOrderByDayNumberAsc(Long productId);
    Optional<ProductScheduleEntity> findByIdAndIsDeletedFalse(Long id);
}
