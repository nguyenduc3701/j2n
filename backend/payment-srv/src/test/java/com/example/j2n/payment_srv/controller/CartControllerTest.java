package com.example.j2n.payment_srv.controller;

import com.example.j2n.payment_srv.dto.request.CartItemRequest;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import com.example.j2n.payment_srv.service.CartService;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.example.j2n.payment_srv.filter.InternalAuthFilter;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private InternalAuthFilter internalAuthFilter;

    @MockBean
    private ConnectionFactory connectionFactory;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void getAllCartItems_shouldReturn200() throws Exception {
        when(cartService.getAllCartItems()).thenReturn(ResponseFactory.success(Collections.emptyList()));

        mockMvc.perform(get("/payment/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    void getCartByUserId_shouldReturn200() throws Exception {
        when(cartService.getCartByUserId("user1")).thenReturn(ResponseFactory.success(Collections.emptyList()));

        mockMvc.perform(get("/payment/carts/user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    void addToCart_shouldReturn200() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("user1");
        request.setItemId("item1");
        request.setItemType("type1");
        request.setQuantity(1);

        when(cartService.addToCart(org.mockito.ArgumentMatchers.any())).thenReturn(ResponseFactory.success(new CartItemEntity()));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/payment/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user1\", \"itemId\":\"item1\", \"itemType\":\"type1\", \"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    void deleteCartItems_shouldReturn200() throws Exception {
        when(cartService.deleteCartItems("user1", Collections.singletonList(1L))).thenReturn(ResponseFactory.success(null));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/payment/carts/user1")
                .param("ids", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }
}
