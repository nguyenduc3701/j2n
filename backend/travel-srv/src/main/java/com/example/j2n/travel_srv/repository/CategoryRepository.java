package com.example.j2n.travel_srv.repository;

import com.example.j2n.travel_srv.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByIsDeletedFalse();
    Optional<CategoryEntity> findBySlug(String slug);
    Optional<CategoryEntity> findByName(String name);
}
