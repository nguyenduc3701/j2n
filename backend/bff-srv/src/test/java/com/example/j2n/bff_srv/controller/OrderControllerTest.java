package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.OrderItemRequest;
import com.example.j2n.bff_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.bff_srv.interceptor.JwtDecodeInterceptor;
import com.example.j2n.bff_srv.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtDecodeInterceptor jwtDecodeInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/bff/order";

    @BeforeEach
    void setUp() throws Exception {
        when(jwtDecodeInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void getAllCartItems_ShouldReturnSuccess() throws Exception {
        Object expectedResponse = Collections.singletonMap("data", "list");
        when(orderService.getAllOrderItems()).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_URL + "/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOrderItems_ShouldReturnSuccess() throws Exception {
        Object expectedResponse = Collections.singletonMap("data", "list");
        when(orderService.getAllOrderItems()).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderByUserId_ShouldReturnSuccess() throws Exception {
        String userId = "user-123";
        Object expectedResponse = Collections.singletonMap("data", "user-orders");
        when(orderService.getOrderByUserId(userId)).thenReturn(expectedResponse);

        mockMvc.perform(get(BASE_URL + "/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addToOrder_ShouldReturnSuccess() throws Exception {
        OrderItemRequest request = OrderItemRequest.builder()
                .userId("user-123")
                .itemId("product-789")
                .itemType("PRODUCT")
                .quantity(2)
                .size("M")
                .design("Classic")
                .metadata(Map.of("pax", 2))
                .build();
        Object expectedResponse = Collections.singletonMap("data", "added");
        when(orderService.addToOrder(any(OrderItemRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateQuantity_ShouldReturnSuccess() throws Exception {
        String userId = "user-123";
        UpdateOrderItemRequest request = UpdateOrderItemRequest.builder()
                .itemId("product-789")
                .itemType("PRODUCT")
                .quantity(3)
                .size("L")
                .design("Modern")
                .build();
        Object expectedResponse = Collections.singletonMap("data", "updated");
        when(orderService.updateQuantity(eq(userId), any(UpdateOrderItemRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put(BASE_URL + "/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrderItems_ShouldReturnSuccess() throws Exception {
        String userId = "user-123";
        List<Long> ids = List.of(1L, 2L);
        Object expectedResponse = Collections.singletonMap("data", "deleted");
        when(orderService.deleteOrderItems(userId, ids)).thenReturn(expectedResponse);

        mockMvc.perform(delete(BASE_URL + "/{userId}", userId)
                        .with(csrf())
                        .param("ids", "1,2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
