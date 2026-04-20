package com.example.j2n.payment_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.CartItemRepository;
import com.example.j2n.payment_srv.repository.entity.CartItemEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    @LogAround(message = "Get all cart items")
    public BaseResponse<List<CartItemEntity>> getAllCartItems() {
        return ResponseFactory.success(cartItemRepository.findAll());
    }
}
