package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.OrderItemRequest;
import com.example.j2n.bff_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RestClientUtil restClientUtil;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrderItems_Success() {
        when(restClientUtil.request(
                eq(GatewayPath.ORDER_BASE_PATH),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.getAllOrderItems();

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(GatewayPath.ORDER_BASE_PATH),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getOrderByUserId_Success() {
        String userId = "user-123";
        String expectedPath = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);

        when(restClientUtil.request(
                eq(expectedPath),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.getOrderByUserId(userId);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(expectedPath),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void addToOrder_Success() {
        OrderItemRequest request = OrderItemRequest.builder()
                .userId("user-123")
                .itemId("product-789")
                .itemType("PRODUCT")
                .quantity(2)
                .size("M")
                .design("Classic")
                .metadata(Map.of("pax", 2))
                .build();

        when(restClientUtil.request(
                eq(GatewayPath.ORDER_BASE_PATH),
                eq(HttpMethod.POST),
                eq(request),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.addToOrder(request);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(GatewayPath.ORDER_BASE_PATH),
                eq(HttpMethod.POST),
                eq(request),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void updateQuantity_Success() {
        String userId = "user-123";
        String expectedPath = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);
        UpdateOrderItemRequest request = UpdateOrderItemRequest.builder()
                .itemId("product-789")
                .itemType("PRODUCT")
                .quantity(3)
                .size("L")
                .design("Modern")
                .build();

        when(restClientUtil.request(
                eq(expectedPath),
                eq(HttpMethod.PUT),
                eq(request),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.updateQuantity(userId, request);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(expectedPath),
                eq(HttpMethod.PUT),
                eq(request),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void deleteOrderItems_WithIds_Success() {
        String userId = "user-123";
        List<Long> ids = List.of(1L, 2L);
        String expectedPath = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId) + "?ids=1,2";

        when(restClientUtil.request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.deleteOrderItems(userId, ids);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void deleteOrderItems_WithEmptyIds_Success() {
        String userId = "user-123";
        List<Long> ids = List.of();
        String expectedPath = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);

        when(restClientUtil.request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.deleteOrderItems(userId, ids);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void deleteOrderItems_WithNullIds_Success() {
        String userId = "user-123";
        String expectedPath = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);

        when(restClientUtil.request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new Object());

        Object result = orderService.deleteOrderItems(userId, null);

        assertNotNull(result);
        verify(restClientUtil).request(
                eq(expectedPath),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }
}
