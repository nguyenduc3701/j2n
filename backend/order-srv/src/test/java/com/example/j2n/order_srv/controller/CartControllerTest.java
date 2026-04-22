package com.example.j2n.order_srv.controller;

import com.example.j2n.order_srv.controller.request.CartItemRequest;
import com.example.j2n.order_srv.controller.request.UpdateCartItemRequest;
import com.example.j2n.order_srv.repository.entity.CartItemEntity;
import com.example.j2n.order_srv.service.CartService;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(controllers = CartController.class, 
    excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class},
    properties = {"internal.token=test-token"})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/order/carts";

    @Test
    void getAllCartItems_ShouldReturnOk() throws Exception {
        when(cartService.getAllCartItems()).thenReturn(ResponseFactory.success(Collections.emptyList()));

        mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCartByUserId_ShouldReturnOk() throws Exception {
        String userId = "user-123";
        when(cartService.getCartByUserId(userId)).thenReturn(ResponseFactory.success(Collections.emptyList()));

        mockMvc.perform(get(BASE_URL + "/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addToCart_ShouldReturnOk() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("user-123");
        request.setItemId("item-1");
        request.setItemType("PRODUCT");
        request.setQuantity(1);

        when(cartService.addToCart(any(CartItemRequest.class))).thenReturn(ResponseFactory.success(new CartItemEntity()));

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateQuantity_ShouldReturnOk() throws Exception {
        String userId = "user-123";
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setItemId("item-1");
        request.setItemType("PRODUCT");
        request.setQuantity(5);

        when(cartService.updateQuantity(any(UpdateCartItemRequest.class), eq(userId)))
                .thenReturn(ResponseFactory.success(new CartItemEntity()));

        mockMvc.perform(put(BASE_URL + "/{userId}", userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCartItems_ShouldReturnOk() throws Exception {
        String userId = "user-123";
        List<Long> ids = List.of(1L, 2L);

        when(cartService.deleteCartItems(eq(userId), eq(ids))).thenReturn(ResponseFactory.success(null));

        mockMvc.perform(delete(BASE_URL + "/{userId}", userId)
                .with(csrf())
                .param("ids", "1", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
