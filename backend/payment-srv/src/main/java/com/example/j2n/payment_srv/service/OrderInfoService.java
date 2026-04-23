package com.example.j2n.payment_srv.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.payment_srv.messaging.order.event.OrderItemCreatedEvent;
import com.example.j2n.payment_srv.messaging.order.event.OrderItemUpdatedEvent;
import com.example.j2n.payment_srv.repository.OrderInfoRepository;
import com.example.j2n.payment_srv.repository.entity.OrderInfoEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInfoService {

    private final OrderInfoRepository orderInfoRepository;

    @Transactional
    @LogAround(message = "Sync User Order Created")
    public void syncOrderCreated(OrderItemCreatedEvent event) {
        OrderInfoEntity entity = OrderInfoEntity.builder()
                .userId(event.getUserId())
                .itemId(event.getItemId())
                .itemType(event.getItemType())
                .quantity(event.getQuantity())
                .updatedAt(LocalDateTime.now())
                .build();
        orderInfoRepository.save(entity);
    }

    @Transactional
    @LogAround(message = "Sync User Order Updated")
    public void syncOrderUpdated(OrderItemUpdatedEvent event) {
        orderInfoRepository.findByUserIdAndItemIdAndItemType(
                event.getUserId(), event.getItemId(), event.getItemType())
                .ifPresent(entity -> {
                    entity.setQuantity(event.getQuantity());
                    entity.setUpdatedAt(LocalDateTime.now());
                    orderInfoRepository.save(entity);
                });
    }

    @Transactional
    @LogAround(message = "Sync User Order Deleted")
    public void syncOrderDeleted(String userId, String itemId, String itemType) {
        orderInfoRepository.deleteByUserIdAndItemIdAndItemType(userId, itemId, itemType);
    }

    public List<OrderInfoEntity> getOrdersByIdsAndUserId(List<Long> orderIds, String userId) {
        return orderInfoRepository.findByIdInAndUserId(orderIds, userId);
    }
}
