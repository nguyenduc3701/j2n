package com.example.j2n.payment_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.payment_srv.constant.MessageEnum;
import com.example.j2n.payment_srv.dto.request.CartItemRequest;
import com.example.j2n.payment_srv.repository.CartItemRepository;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    @LogAround(message = "Get all cart items")
    public BaseResponse<List<CartItemEntity>> getAllCartItems() {
        return ResponseFactory.success(cartItemRepository.findAll());
    }

    @LogAround(message = "Get cart items by user ID")
    public BaseResponse<List<CartItemEntity>> getCartByUserId(String userId) {
        return ResponseFactory.success(findByUserIdOrThrow(userId));
    }

    @Transactional
    @LogAround(message = "Add to cart")
    public BaseResponse<CartItemEntity> addToCart(CartItemRequest request) {
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

        return ResponseFactory.success(cartItemRepository.save(item));
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
}
