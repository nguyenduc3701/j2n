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
        validateStock(request.getItemId(), request.getQuantity());

        OrderItemEntity savedItem = switch (request.getItemType()) {
            case "STORE" -> handleStoreItem(request);
            case "TOUR" -> handleTourItem(request);
            default -> handleDefaultItem(request);
        };

        return ResponseFactory.of(MessageEnum.ADD_TO_ORDER_SUCCESS, savedItem);
    }

    private OrderItemEntity handleStoreItem(OrderItemRequest request) {
        log.info("[ORDER-SRV] Handling STORE item: {} for user: {}", request.getItemId(), request.getUserId());
        Optional<OrderItemEntity> existingItem = orderItemRepository.findByUserIdAndItemIdAndItemTypeAndSizeAndDesign(
                request.getUserId(), request.getItemId(), request.getItemType(), request.getSize(), request.getDesign());
        return saveOrUpdate(existingItem, request);
    }

    private OrderItemEntity handleTourItem(OrderItemRequest request) {
        log.info("[ORDER-SRV] Handling TOUR item: {} for user: {}", request.getItemId(), request.getUserId());
        // For TOUR, we currently use standard lookup. Specific logic can be added here (e.g., checking date in metadata)
        Optional<OrderItemEntity> existingItem = orderItemRepository.findByUserIdAndItemIdAndItemType(
                request.getUserId(), request.getItemId(), request.getItemType());
        return saveOrUpdate(existingItem, request);
    }

    private OrderItemEntity handleDefaultItem(OrderItemRequest request) {
        log.info("[ORDER-SRV] Handling generic item type: {} for user: {}", request.getItemType(), request.getUserId());
        Optional<OrderItemEntity> existingItem = orderItemRepository.findByUserIdAndItemIdAndItemType(
                request.getUserId(), request.getItemId(), request.getItemType());
        return saveOrUpdate(existingItem, request);
    }

    private OrderItemEntity saveOrUpdate(Optional<OrderItemEntity> existing, OrderItemRequest request) {
        OrderItemEntity item;
        boolean isNew = existing.isEmpty();

        if (existing.isPresent()) {
            item = existing.get();
            item.setQuantity(request.getQuantity());
            if (request.getMetadata() != null) {
                item.setMetadata(request.getMetadata());
            }
            item.setSize(request.getSize());
            item.setDesign(request.getDesign());
        } else {
            item = OrderItemEntity.builder()
                    .userId(request.getUserId())
                    .itemId(request.getItemId())
                    .itemType(request.getItemType())
                    .quantity(request.getQuantity())
                    .size(request.getSize())
                    .design(request.getDesign())
                    .metadata(request.getMetadata())
                    .build();
        }

        OrderItemEntity saved = orderItemRepository.save(item);
        if (isNew) {
            publishOrderItemCreated(saved);
        } else {
            publishOrderItemUpdated(saved);
        }
        return saved;
    }

    @Transactional
    @LogAround(message = "Update order item quantity")
    public BaseResponse<OrderItemEntity> updateQuantity(UpdateOrderItemRequest request, String userId) {
        validateUnknownFields(request);
        OrderItemEntity item = findOrderItemOrThrow(userId, request.getItemId(), request.getItemType(), request.getSize(), request.getDesign());
        if (request.getQuantity() == 0) {
            orderItemRepository.delete(item);
            publishOrderItemDeleted(item);
            return ResponseFactory.of(MessageEnum.UPDATE_ORDER_SUCCESS, null);
        }
        item.setQuantity(request.getQuantity());
        item.setSize(request.getSize());
        item.setDesign(request.getDesign());
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
        orderItemRepository.deleteAll(items);
        items.stream()
                .filter(item -> item.getUserId().equals(userId))
                .forEach(this::publishOrderItemDeleted);
        return ResponseFactory.success(null);
    }

    @Transactional
    @LogAround(message = "Clear cart after payment")
    public void clearCartAfterPayment(String userId) {
        log.info("[ORDER-SRV] Clearing cart for user: {}", userId);
        List<OrderItemEntity> items = orderItemRepository.findByUserId(userId);
        if (items.isEmpty()) {
            log.info("[ORDER-SRV] Cart already empty for user: {}", userId);
            return;
        }

        orderItemRepository.deleteAll(items);
        items.forEach(this::publishOrderItemDeleted);
        log.info("[ORDER-SRV] Soft-deleted {} order items for userId: {}", items.size(), userId);
    }

    private void publishOrderItemCreated(OrderItemEntity entity) {
        log.info("Publishing order created event for item id: {}", entity.getId());
        orderEventPublisher.publishOrderCreated(OrderItemCreatedEvent.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .itemId(entity.getItemId())
                .itemType(entity.getItemType())
                .quantity(entity.getQuantity())
                .size(entity.getSize())
                .design(entity.getDesign())
                .build());
    }

    private void publishOrderItemUpdated(OrderItemEntity entity) {
        log.info("Publishing order updated event for item id: {}", entity.getId());
        orderEventPublisher.publishOrderUpdated(OrderItemUpdatedEvent.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .itemId(entity.getItemId())
                .itemType(entity.getItemType())
                .quantity(entity.getQuantity())
                .size(entity.getSize())
                .design(entity.getDesign())
                .build());
    }

    private void publishOrderItemDeleted(OrderItemEntity entity) {
        log.info("Publishing order deleted event for item id: {}", entity.getId());
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

    private OrderItemEntity findOrderItemOrThrow(String userId, String itemId, String itemType, String size, String design) {
        Optional<OrderItemEntity> item = "STORE".equals(itemType)
                ? orderItemRepository.findByUserIdAndItemIdAndItemTypeAndSizeAndDesign(userId, itemId, itemType, size, design)
                : orderItemRepository.findByUserIdAndItemIdAndItemType(userId, itemId, itemType);

        return item.orElseThrow(() -> {
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
                .size(event.getSize())
                .design(event.getDesign())
                .isDeleted(event.getIsDeleted())
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
                    entity.setSize(event.getSize());
                    entity.setDesign(event.getDesign());
                    entity.setIsDeleted(event.getIsDeleted());
                    productInfoRepository.save(entity);
                });
    }

    @Transactional
    @LogAround(message = "Sync Product Deleted")
    public void syncProductDeleted(ProductDeletedEvent event) {
        productInfoRepository.findById(Long.valueOf(event.getProductId()))
                .ifPresent(entity -> {
                    entity.setIsDeleted(true);
                    productInfoRepository.save(entity);
                });
    }

    private void validateStock(String itemId, Integer requestedQuantity) {
        log.info("Validating stock for item id: {} and quantity: {}", itemId, requestedQuantity);
        ProductInfoEntity product = productInfoRepository.findById(Long.valueOf(itemId))
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.PRODUCT_NOT_FOUND.withArgs(itemId)));

        int availableStock = (product.getStock() != null ? product.getStock() : 0)
                - (product.getLockedStock() != null ? product.getLockedStock() : 0);

        if (availableStock < requestedQuantity) {
            log.warn("[ORDER-SRV] Insufficient stock for product: {}. Available: {}, Requested: {}",
                    product.getTitle(), availableStock, requestedQuantity);
            throw new InvalidInputException(
                    MessageEnum.INSUFFICIENT_STOCK.withArgs(product.getTitle(), availableStock));
        }
    }
}
