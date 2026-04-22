package com.example.j2n.product_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.j2n.product_srv.repository.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByIsDeletedFalse();
    Optional<CategoryEntity> findByIdAndIsDeletedFalse(Long id);
    Optional<CategoryEntity> findBySlugAndIsDeletedFalse(String slug);
    Optional<CategoryEntity> findByNameAndIsDeletedFalse(String name);
}
