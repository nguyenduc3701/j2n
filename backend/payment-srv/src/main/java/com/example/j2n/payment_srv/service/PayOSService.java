package com.example.j2n.payment_srv.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.j2n.payment_srv.repository.entity.OrderInfoEntity;
import com.example.j2n.payment_srv.repository.entity.ProductInfoEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.payment_srv.constant.MessageEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayOSService {

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    private final PayOS payOS;

    @LogAround(message = "Create PayOS payment link")
    public CreatePaymentLinkResponse createPaymentLink(Long orderCode, BigDecimal total, List<OrderInfoEntity> orders,
            List<ProductInfoEntity> products) {
        List<PaymentLinkItem> items = buildPaymentLinkItems(orders, products);
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(total.longValue())
                .description("Payment for Order #" + orderCode)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .items(items)
                .build();

        try {
            CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
            return response;
        } catch (Exception e) {
            throw new InvalidInputException(MessageEnum.PAYMENT_PROVIDER_ERROR.withArgs(e.getMessage()));
        }
    }

    public List<PaymentLinkItem> buildPaymentLinkItems(List<OrderInfoEntity> orders, List<ProductInfoEntity> products) {
        List<PaymentLinkItem> items = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            OrderInfoEntity order = orders.get(i);
            ProductInfoEntity product = products.get(i);
            items.add(buildPaymentLinkItem(product.getTitle(), order.getQuantity(), product.getPrice()));
        }
        return items;
    }

    public PaymentLinkItem buildPaymentLinkItem(String name, int quantity, BigDecimal price) {
        return PaymentLinkItem.builder()
                .name(name)
                .quantity(quantity)
                .price(price.longValue())
                .build();
    }
}
