package com.example.j2n.order_srv.repository;

import com.example.j2n.order_srv.repository.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserId(String userId);

    Optional<CartItemEntity> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);

    void deleteByUserIdAndIdIn(String userId, List<Long> ids);
}
