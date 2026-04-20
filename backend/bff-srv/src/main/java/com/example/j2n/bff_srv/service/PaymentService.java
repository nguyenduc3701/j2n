package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RestClientUtil restClientUtil;

    public Object getAllCartItems() {
        log.info("[Start] Get all cart items from payment-srv");
        return restClientUtil.request(
                GatewayPath.PAYMENT_CARTS_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    public Object getAllTransactions() {
        log.info("[Start] Get all transactions from payment-srv");
        return restClientUtil.request(
                GatewayPath.PAYMENT_TRANSACTIONS_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }
}
