package com.example.j2n.payment_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.payment_srv.dto.request.CartItemRequest;
import com.example.j2n.payment_srv.repository.CartItemRepository;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void getAllCartItems_shouldReturnBaseResponse() {
        // Arrange
        CartItemEntity item1 = CartItemEntity.builder().id(1L).userId("user1").build();
        CartItemEntity item2 = CartItemEntity.builder().id(2L).userId("user1").build();
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        // Act
        BaseResponse<List<CartItemEntity>> result = cartService.getAllCartItems();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
    }

    @Test
    void getCartByUserId_shouldReturnItems_whenUserHasItems() {
        String userId = "user1";
        CartItemEntity item = CartItemEntity.builder().id(1L).userId(userId).build();
        when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.singletonList(item));

        BaseResponse<List<CartItemEntity>> result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }

    @Test
    void getCartByUserId_shouldThrowException_whenUserHasNoItems() {
        String userId = "user1";
        when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        assertThrows(DataNotFoundException.class, () -> cartService.getCartByUserId(userId));
    }

    @Test
    void addToCart_shouldUpdateQuantity_whenItemExists() {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("user1");
        request.setItemId("item1");
        request.setItemType("type1");
        request.setQuantity(2);
        request.setMetadata("some meta");

        CartItemEntity existingItem = CartItemEntity.builder()
                .userId("user1").itemId("item1").itemType("type1").quantity(1).build();

        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any()))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> result = cartService.addToCart(request);

        assertEquals(3, result.getData().getQuantity());
        assertEquals("some meta", result.getData().getMetadata());
    }

    @Test
    void addToCart_shouldCreateNew_whenItemNotExists() {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("user1");
        request.setItemId("item1");
        request.setItemType("type1");
        request.setQuantity(2);

        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> result = cartService.addToCart(request);

        assertEquals(2, result.getData().getQuantity());
        assertEquals("user1", result.getData().getUserId());
    }

    @Test
    void addToCart_shouldPreserveMetadata_whenRequestMetadataIsNull() {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("user1");
        request.setItemId("item1");
        request.setItemType("type1");
        request.setQuantity(2);
        request.setMetadata(null);

        CartItemEntity existingItem = CartItemEntity.builder()
                .userId("user1").itemId("item1").itemType("type1").quantity(1)
                .metadata("old meta").build();

        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any()))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> result = cartService.addToCart(request);

        assertEquals(3, result.getData().getQuantity());
        assertEquals("old meta", result.getData().getMetadata());
    }

    @Test
    void deleteCartItems_shouldDelete_whenIdsValid() {
        String userId = "user1";
        List<Long> ids = Arrays.asList(1L, 2L);

        BaseResponse<Void> result = cartService.deleteCartItems(userId, ids);

        verify(cartItemRepository, times(1)).deleteByUserIdAndIdIn(userId, ids);
        assertNotNull(result);
    }

    @Test
    void deleteCartItems_shouldThrowException_whenIdsNullOrEmpty() {
        assertThrows(InvalidInputException.class, () -> cartService.deleteCartItems("user1", null));
        assertThrows(InvalidInputException.class, () -> cartService.deleteCartItems("user1", Collections.emptyList()));
    }
}
