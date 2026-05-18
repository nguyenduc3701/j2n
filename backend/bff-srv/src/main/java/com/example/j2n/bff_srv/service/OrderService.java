package com.example.j2n.bff_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.OrderItemRequest;
import com.example.j2n.bff_srv.controller.request.UpdateOrderItemRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RestClientUtil restClientUtil;

    @LogAround(message = "Get all order items")
    public Object getAllOrderItems() {
        log.info("[Start] Get all order items from order-srv");
        return restClientUtil.request(
                GatewayPath.ORDER_BASE_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    @LogAround(message = "Get order items by user ID")
    public Object getOrderByUserId(String userId) {
        log.info("[Start] Get order items for userId: {} from order-srv", userId);
        String path = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);
        return restClientUtil.request(
                path,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    @LogAround(message = "Add item to order")
    public Object addToOrder(OrderItemRequest request) {
        log.info("[Start] Add item to order for userId: {} in order-srv", request.getUserId());
        return restClientUtil.request(
                GatewayPath.ORDER_BASE_PATH,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    @LogAround(message = "Update quantity in order")
    public Object updateQuantity(String userId, UpdateOrderItemRequest request) {
        log.info("[Start] Update quantity for userId: {} in order-srv", userId);
        String path = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);
        return restClientUtil.request(
                path,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    @LogAround(message = "Delete items from order")
    public Object deleteOrderItems(String userId, List<Long> ids) {
        log.info("[Start] Delete order items for userId: {} with ids: {} from order-srv", userId, ids);
        String path = String.format(GatewayPath.ORDER_BY_USER_ID_PATH, userId);
        if (ids != null && !ids.isEmpty()) {
            String idsParam = ids.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            path += "?ids=" + idsParam;
        }
        return restClientUtil.request(
                path,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }
}
