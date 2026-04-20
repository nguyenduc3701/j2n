package com.example.j2n.payment_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.CartItemRepository;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        assertEquals(1L, result.getData().get(0).getId());
        assertEquals("user1", result.getData().get(1).getUserId());
    }
}
