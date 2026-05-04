package com.example.j2n.payment_srv.repository;

import com.example.j2n.payment_srv.repository.entity.OrderInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfoEntity, Long> {
    List<OrderInfoEntity> findByUserId(String userId);
    List<OrderInfoEntity> findByIdInAndUserId(List<Long> ids, String userId);
    Optional<OrderInfoEntity> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
}
