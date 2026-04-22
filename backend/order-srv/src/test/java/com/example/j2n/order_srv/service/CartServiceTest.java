package com.example.j2n.order_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.order_srv.controller.request.CartItemRequest;
import com.example.j2n.order_srv.controller.request.UpdateCartItemRequest;
import com.example.j2n.order_srv.dto.response.CartItemWithProductResponse;
import com.example.j2n.order_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.order_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.order_srv.repository.CartItemRepository;
import com.example.j2n.order_srv.repository.ProductInfoRepository;
import com.example.j2n.order_srv.repository.entity.CartItemEntity;
import com.example.j2n.order_srv.repository.entity.ProductInfoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductInfoRepository productInfoRepository;

    @InjectMocks
    private CartService cartService;

    private CartItemEntity mockItem;
    private ProductInfoEntity mockProduct;
    private CartItemRequest mockAddRequest;
    private UpdateCartItemRequest mockUpdateRequest;

    @BeforeEach
    void setUp() {
        mockItem = CartItemEntity.builder()
                .id(1L)
                .userId("user-123")
                .itemId("1")
                .itemType("PRODUCT")
                .quantity(2)
                .metadata(Map.of("color", "red"))
                .build();

        mockProduct = ProductInfoEntity.builder()
                .id(1L)
                .title("Test Product")
                .price(BigDecimal.valueOf(100))
                .thumbnail("test.jpg")
                .build();

        mockAddRequest = new CartItemRequest();
        mockAddRequest.setUserId("user-123");
        mockAddRequest.setItemId("1");
        mockAddRequest.setItemType("PRODUCT");
        mockAddRequest.setQuantity(2);
        mockAddRequest.setMetadata(Map.of("color", "red"));

        mockUpdateRequest = new UpdateCartItemRequest();
        mockUpdateRequest.setItemId("1");
        mockUpdateRequest.setItemType("PRODUCT");
        mockUpdateRequest.setQuantity(5);
    }

    @Test
    void getAllCartItems_ShouldReturnEnrichedList() {
        when(cartItemRepository.findAll()).thenReturn(List.of(mockItem));
        when(productInfoRepository.findAllById(any())).thenReturn(List.of(mockProduct));

        BaseResponse<List<CartItemWithProductResponse>> response = cartService.getAllCartItems();

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertNotNull(response.getData().get(0).getProduct());
        assertEquals("Test Product", response.getData().get(0).getProduct().getTitle());
        verify(cartItemRepository).findAll();
    }

    @Test
    void getCartByUserId_Success() {
        when(cartItemRepository.findByUserId("user-123")).thenReturn(List.of(mockItem));
        when(productInfoRepository.findAllById(any())).thenReturn(List.of(mockProduct));

        BaseResponse<List<CartItemWithProductResponse>> response = cartService.getCartByUserId("user-123");

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("Test Product", response.getData().get(0).getProduct().getTitle());
        verify(cartItemRepository).findByUserId("user-123");
    }

    @Test
    void getCartByUserId_NotFound_ThrowsException() {
        when(cartItemRepository.findByUserId("user-123")).thenReturn(Collections.emptyList());

        assertThrows(DataNotFoundException.class, () -> cartService.getCartByUserId("user-123"));
    }

    @Test
    void addToCart_NewItem_Success() {
        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItemEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> response = cartService.addToCart(mockAddRequest);

        assertNotNull(response.getData());
        assertEquals("user-123", response.getData().getUserId());
        assertEquals(2, response.getData().getQuantity());
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void addToCart_ExistingItem_IncreasesQuantity() {
        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any())).thenReturn(Optional.of(mockItem));
        when(cartItemRepository.save(any(CartItemEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> response = cartService.addToCart(mockAddRequest);

        assertNotNull(response.getData());
        assertEquals(4, response.getData().getQuantity()); // 2 + 2
        verify(cartItemRepository).save(mockItem);
    }

    @Test
    void updateQuantity_Success() {
        when(cartItemRepository.findByUserIdAndItemIdAndItemType(any(), any(), any())).thenReturn(Optional.of(mockItem));
        when(cartItemRepository.save(any(CartItemEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<CartItemEntity> response = cartService.updateQuantity(mockUpdateRequest, "user-123");

        assertNotNull(response.getData());
        assertEquals(5, response.getData().getQuantity());
        verify(cartItemRepository).save(mockItem);
    }

    @Test
    void syncProductCreated_Success() {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId("1")
                .title("New Product")
                .price(BigDecimal.valueOf(200))
                .thumbnail("new.jpg")
                .build();

        cartService.syncProductCreated(event);

        verify(productInfoRepository).save(any(ProductInfoEntity.class));
    }

    @Test
    void syncProductUpdated_Success() {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId("1")
                .title("Updated Title")
                .price(BigDecimal.valueOf(150))
                .thumbnail("updated.jpg")
                .build();

        when(productInfoRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        cartService.syncProductUpdated(event);

        assertEquals("Updated Title", mockProduct.getTitle());
        assertEquals(BigDecimal.valueOf(150), mockProduct.getPrice());
        verify(productInfoRepository).save(mockProduct);
    }

    @Test
    void syncProductDeleted_Success() {
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId("1")
                .build();

        cartService.syncProductDeleted(event);

        verify(productInfoRepository).deleteById(1L);
    }

    @Test
    void validateUnknownFields_ThrowsException() {
        CartItemRequest requestWithUnknown = new CartItemRequest() {
            @Override
            public boolean hasUnknownFields() { return true; }
            @Override
            public List<String> getUnknownFields() { return List.of("bad"); }
        };
        
        assertThrows(UnknowFieldException.class, () -> cartService.addToCart(requestWithUnknown));
    }
}
