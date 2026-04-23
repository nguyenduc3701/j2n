package com.example.j2n.order_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.order_srv.constant.MessageEnum;
import com.example.j2n.order_srv.controller.request.OrderItemRequest;
import com.example.j2n.order_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.dto.BaseRequest;
import com.example.j2n.order_srv.messaging.order.event.OrderItemCreatedEvent;
import com.example.j2n.order_srv.messaging.order.event.OrderItemDeletedEvent;
import com.example.j2n.order_srv.messaging.order.event.OrderItemUpdatedEvent;
import com.example.j2n.order_srv.messaging.order.publisher.OrderEventPublisher;
import com.example.j2n.order_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.order_srv.repository.OrderItemRepository;
import com.example.j2n.order_srv.repository.ProductInfoRepository;
import com.example.j2n.order_srv.repository.entity.OrderItemEntity;
import com.example.j2n.order_srv.repository.entity.ProductInfoEntity;
import com.example.j2n.order_srv.service.response.OrderItemWithProductResponse;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final ProductInfoRepository productInfoRepository;
    private final OrderEventPublisher orderEventPublisher;

    @LogAround(message = "Get all order items with product details")
    public BaseResponse<List<OrderItemWithProductResponse>> getAllOrderItems() {
        List<OrderItemEntity> items = orderItemRepository.findAll();
        return ResponseFactory.success(enrichOrderItems(items));
    }

    @LogAround(message = "Get order items by user ID with product details")
    public BaseResponse<List<OrderItemWithProductResponse>> getOrderByUserId(String userId) {
        List<OrderItemEntity> items = findByUserIdOrThrow(userId);
        return ResponseFactory.success(enrichOrderItems(items));
    }

    @Transactional
    @LogAround(message = "Add to order")
    public BaseResponse<OrderItemEntity> addToOrder(OrderItemRequest request) {
        validateUnknownFields(request);
        Optional<OrderItemEntity> existingItem = orderItemRepository.findByUserIdAndItemIdAndItemType(
                request.getUserId(), request.getItemId(), request.getItemType());

        OrderItemEntity item;
        boolean isNew = existingItem.isEmpty();
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            if (request.getMetadata() != null) {
                item.setMetadata(request.getMetadata());
            }
        } else {
            item = OrderItemEntity.builder()
                    .userId(request.getUserId())
                    .itemId(request.getItemId())
                    .itemType(request.getItemType())
                    .quantity(request.getQuantity())
                    .metadata(request.getMetadata())
                    .build();
        }

        OrderItemEntity saved = orderItemRepository.save(item);
        if (isNew) {
            publishOrderItemCreated(saved);
        } else {
            publishOrderItemUpdated(saved);
        }
        return ResponseFactory.of(MessageEnum.ADD_TO_ORDER_SUCCESS, saved);
    }

    @Transactional
    @LogAround(message = "Update order item quantity")
    public BaseResponse<OrderItemEntity> updateQuantity(UpdateOrderItemRequest request, String userId) {
        validateUnknownFields(request);
        OrderItemEntity item = findOrderItemOrThrow(userId, request.getItemId(), request.getItemType());
        if (request.getQuantity() == 0) {
            orderItemRepository.delete(item);
            publishOrderItemDeleted(item);
            return ResponseFactory.of(MessageEnum.UPDATE_ORDER_SUCCESS, null);
        }
        item.setQuantity(request.getQuantity());
        OrderItemEntity saved = orderItemRepository.save(item);
        publishOrderItemUpdated(saved);
        return ResponseFactory.of(MessageEnum.UPDATE_ORDER_SUCCESS, saved);
    }

    @Transactional
    @LogAround(message = "Delete order items")
    public BaseResponse<Void> deleteOrderItems(String userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidInputException(MessageEnum.DELETE_ORDER_ITEMS_SHOULD_NOT_BE_EMPTY);
        }
        List<OrderItemEntity> items = orderItemRepository.findAllById(ids);
        orderItemRepository.deleteByUserIdAndIdIn(userId, ids);
        items.stream()
                .filter(item -> item.getUserId().equals(userId))
                .forEach(this::publishOrderItemDeleted);
        return ResponseFactory.success(null);
    }

    private void publishOrderItemCreated(OrderItemEntity entity) {
        orderEventPublisher.publishOrderCreated(OrderItemCreatedEvent.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .itemId(entity.getItemId())
                .itemType(entity.getItemType())
                .quantity(entity.getQuantity())
                .build());
    }

    private void publishOrderItemUpdated(OrderItemEntity entity) {
        orderEventPublisher.publishOrderUpdated(OrderItemUpdatedEvent.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .itemId(entity.getItemId())
                .itemType(entity.getItemType())
                .quantity(entity.getQuantity())
                .build());
    }

    private void publishOrderItemDeleted(OrderItemEntity entity) {
        orderEventPublisher.publishOrderDeleted(OrderItemDeletedEvent.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .itemId(entity.getItemId())
                .itemType(entity.getItemType())
                .build());
    }

    private List<OrderItemEntity> findByUserIdOrThrow(String userId) {
        List<OrderItemEntity> items = orderItemRepository.findByUserId(userId);
        if (items.isEmpty()) {
            throw new DataNotFoundException(MessageEnum.ORDER_NOT_FOUND.withArgs(userId));
        }
        return items;
    }

    private void validateUnknownFields(BaseRequest request) {
        if (request.hasUnknownFields()) {
            log.error("[ORDER-SRV] Unknown fields in request: {}", request.getUnknownFields());
            throw new UnknowFieldException(BaseMessageEnum.UNKNOWN_FIELDS);
        }
    }

    private OrderItemEntity findOrderItemOrThrow(String userId, String itemId, String itemType) {
        return orderItemRepository.findByUserIdAndItemIdAndItemType(userId, itemId, itemType)
                .orElseThrow(() -> {
                    log.error("[ORDER-SRV] Order item not found for user: {} and item: {}", userId, itemId);
                    return new DataNotFoundException(MessageEnum.ORDER_NOT_FOUND.withArgs(userId));
                });
    }

    private List<OrderItemWithProductResponse> enrichOrderItems(List<OrderItemEntity> items) {
        if (items == null || items.isEmpty()) {
            log.info("[ORDER-SRV] Order is empty");
            return List.of();
        }

        // Collect unique product IDs
        Set<Long> productIds = items.stream()
                .map(item -> {
                    try {
                        return Long.valueOf(item.getItemId());
                    } catch (NumberFormatException e) {
                        log.warn("[ORDER-SRV] Invalid itemId format: {}", item.getItemId());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Bulk fetch product info
        Map<Long, ProductInfoEntity> productInfoMap = productInfoRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(ProductInfoEntity::getId,
                        Function.identity()));

        // Map items to enriched response
        return items.stream()
                .map(item -> {
                    Long productId = null;
                    try {
                        productId = Long.valueOf(item.getItemId());
                    } catch (NumberFormatException ignored) {
                    }

                    ProductInfoEntity productInfo = productId != null ? productInfoMap.get(productId) : null;
                    return OrderItemWithProductResponse.from(item, productInfo);
                })
                .toList();
    }

    @Transactional
    @LogAround(message = "Sync Product Created")
    public void syncProductCreated(ProductCreatedEvent event) {
        ProductInfoEntity entity = ProductInfoEntity.builder()
                .id(Long.valueOf(event.getProductId()))
                .title(event.getTitle())
                .price(event.getPrice())
                .stock(event.getStock())
                .thumbnail(event.getThumbnail())
                .build();
        productInfoRepository.save(entity);
    }

    @Transactional
    @LogAround(message = "Sync Product Updated")
    public void syncProductUpdated(ProductUpdatedEvent event) {
        productInfoRepository.findById(Long.valueOf(event.getProductId()))
                .ifPresent(entity -> {
                    entity.setTitle(event.getTitle());
                    entity.setPrice(event.getPrice());
                    entity.setStock(event.getStock());
                    entity.setThumbnail(event.getThumbnail());
                    productInfoRepository.save(entity);
                });
    }

    @Transactional
    @LogAround(message = "Sync Product Deleted")
    public void syncProductDeleted(ProductDeletedEvent event) {
        productInfoRepository.deleteById(Long.valueOf(event.getProductId()));
    }
}
