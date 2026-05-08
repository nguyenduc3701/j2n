package com.example.j2n.order_srv.repository;

import com.example.j2n.order_srv.repository.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByUserId(String userId);

    Optional<OrderItemEntity> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);

    Optional<OrderItemEntity> findByUserIdAndItemIdAndItemTypeAndSizeAndDesign(
            String userId, String itemId, String itemType, String size, String design);
}
