package com.example.j2n.order_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.order_srv.constant.MessageEnum;
import com.example.j2n.order_srv.controller.request.CartItemRequest;
import com.example.j2n.order_srv.controller.request.UpdateCartItemRequest;
import com.example.j2n.dto.BaseRequest;
import com.example.j2n.order_srv.dto.response.CartItemWithProductResponse;
import com.example.j2n.order_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.order_srv.repository.CartItemRepository;
import com.example.j2n.order_srv.repository.ProductInfoRepository;
import com.example.j2n.order_srv.repository.entity.CartItemEntity;
import com.example.j2n.order_srv.repository.entity.ProductInfoEntity;
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
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductInfoRepository productInfoRepository;

    @LogAround(message = "Get all cart items with product details")
    public BaseResponse<List<CartItemWithProductResponse>> getAllCartItems() {
        List<CartItemEntity> items = cartItemRepository.findAll();
        return ResponseFactory.success(enrichCartItems(items));
    }

    @LogAround(message = "Get cart items by user ID with product details")
    public BaseResponse<List<CartItemWithProductResponse>> getCartByUserId(String userId) {
        List<CartItemEntity> items = findByUserIdOrThrow(userId);
        return ResponseFactory.success(enrichCartItems(items));
    }

    @Transactional
    @LogAround(message = "Add to cart")
    public BaseResponse<CartItemEntity> addToCart(CartItemRequest request) {
        validateUnknownFields(request);
        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserIdAndItemIdAndItemType(
                request.getUserId(), request.getItemId(), request.getItemType());

        CartItemEntity item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            if (request.getMetadata() != null) {
                item.setMetadata(request.getMetadata());
            }
        } else {
            item = CartItemEntity.builder()
                    .userId(request.getUserId())
                    .itemId(request.getItemId())
                    .itemType(request.getItemType())
                    .quantity(request.getQuantity())
                    .metadata(request.getMetadata())
                    .build();
        }

        return ResponseFactory.of(MessageEnum.ADD_TO_CART_SUCCESS, cartItemRepository.save(item));
    }

    @Transactional
    @LogAround(message = "Update cart item quantity")
    public BaseResponse<CartItemEntity> updateQuantity(UpdateCartItemRequest request, String userId) {
        validateUnknownFields(request);
        CartItemEntity item = findCartItemOrThrow(userId, request.getItemId(), request.getItemType());
        if (request.getQuantity() == 0) {
            cartItemRepository.delete(item);
            return ResponseFactory.of(MessageEnum.UPDATE_CART_SUCCESS, null);
        }
        item.setQuantity(request.getQuantity());
        return ResponseFactory.of(MessageEnum.UPDATE_CART_SUCCESS, cartItemRepository.save(item));
    }

    @Transactional
    @LogAround(message = "Delete cart items")
    public BaseResponse<Void> deleteCartItems(String userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidInputException(MessageEnum.DELETE_CART_ITEMS_SHOULD_NOT_BE_EMPTY);
        }
        cartItemRepository.deleteByUserIdAndIdIn(userId, ids);
        return ResponseFactory.success(null);
    }

    private List<CartItemEntity> findByUserIdOrThrow(String userId) {
        List<CartItemEntity> items = cartItemRepository.findByUserId(userId);
        if (items.isEmpty()) {
            throw new DataNotFoundException(MessageEnum.CART_NOT_FOUND.withArgs(userId));
        }
        return items;
    }

    private void validateUnknownFields(BaseRequest request) {
        if (request.hasUnknownFields()) {
            log.error("[ORDER-SRV] Unknown fields in request: {}", request.getUnknownFields());
            throw new UnknowFieldException(BaseMessageEnum.UNKNOWN_FIELDS);
        }
    }

    private CartItemEntity findCartItemOrThrow(String userId, String itemId, String itemType) {
        return cartItemRepository.findByUserIdAndItemIdAndItemType(userId, itemId, itemType)
                .orElseThrow(() -> {
                    log.error("[ORDER-SRV] Cart item not found for user: {} and item: {}", userId, itemId);
                    return new DataNotFoundException(MessageEnum.CART_NOT_FOUND.withArgs(userId));
                });
    }

    private List<CartItemWithProductResponse> enrichCartItems(List<CartItemEntity> items) {
        if (items == null || items.isEmpty()) {
            log.info("[ORDER-SRV] Cart is empty");
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
                    return CartItemWithProductResponse.from(item, productInfo);
                })
                .toList();
    }

    @Transactional
    public void syncProductCreated(ProductCreatedEvent event) {
        ProductInfoEntity entity = ProductInfoEntity.builder()
                .id(Long.valueOf(event.getProductId()))
                .title(event.getTitle())
                .price(event.getPrice())
                .thumbnail(event.getThumbnail())
                .build();
        productInfoRepository.save(entity);
    }

    @Transactional
    public void syncProductUpdated(ProductUpdatedEvent event) {
        productInfoRepository.findById(Long.valueOf(event.getProductId()))
                .ifPresent(entity -> {
                    entity.setTitle(event.getTitle());
                    entity.setPrice(event.getPrice());
                    entity.setThumbnail(event.getThumbnail());
                    productInfoRepository.save(entity);
                });
    }

    @Transactional
    public void syncProductDeleted(ProductDeletedEvent event) {
        productInfoRepository.deleteById(Long.valueOf(event.getProductId()));
    }
}
