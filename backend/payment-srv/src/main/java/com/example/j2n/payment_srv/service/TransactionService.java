package com.example.j2n.payment_srv.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.controller.request.CreateTransactionRequest;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.payment_srv.constant.MessageEnum;
import com.example.j2n.payment_srv.repository.TransactionRepository;
import com.example.j2n.payment_srv.repository.entity.OrderInfoEntity;
import com.example.j2n.payment_srv.repository.entity.ProductInfoEntity;
import com.example.j2n.payment_srv.repository.entity.TransactionEntity;
import com.example.j2n.payment_srv.service.response.TransactionResponse;
import com.example.j2n.utils.ResponseFactory;

import io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderInfoService orderInfoService;
    private final ProductInfoService productInfoService;
    private final PayOSService payOSService;

    @LogAround(message = "Get all transactions")
    public BaseResponse<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionRepository.findAll().stream()
                .map(entity -> TransactionResponse.fromEntity(entity, null)) // Checkout URL is usually generated per
                                                                             // request
                .toList();
        return ResponseFactory.success(transactions);
    }

    @LogAround(message = "Create a transaction")
    @Transactional
    public BaseResponse<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        log.info("Creating transaction for user: {}, amount: {}", request.getUserId(), request.getTotalAmount());

        // 1. Validate and fetch orders
        List<OrderInfoEntity> orders = validateAndGetOrders(request.getOrderIds(), request.getUserId());

        // 2. Validate products (stock, price) and prepare payment items
        ProductValidationResult validationResult = validateProductsAndPrepareItems(orders);

        // 3. Verify total amount consistency
        validateTotalAmount(validationResult.price(), request.getTotalAmount());

        // 4. Generate unique order code for PayOS
        Long orderCode = generateOrderCode();

        // 5. Create Payment Link via PayOS Service
        CreatePaymentLinkResponse payOSResponse = payOSService.createPaymentLink(
                orderCode,
                validationResult.price(),
                orders,
                validationResult.products());

        // 6. Persist transaction record
        TransactionEntity transaction = buildTransactionEntity(request, orderCode);
        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        // 7. Return DTO with checkout URL
        return ResponseFactory
                .success(TransactionResponse.fromEntity(savedTransaction, payOSResponse.getCheckoutUrl()));
    }

    private record ProductValidationResult(List<ProductInfoEntity> products, BigDecimal price) {
    }

    private ProductValidationResult validateProductsAndPrepareItems(List<OrderInfoEntity> orders) {
        List<Long> productIds = orders.stream()
                .map(order -> Long.valueOf(order.getItemId()))
                .toList();

        Map<Long, ProductInfoEntity> productMap = productInfoService.getProductInfos(productIds).stream()
                .collect(Collectors.toMap(ProductInfoEntity::getId, java.util.function.Function.identity()));

        List<ProductInfoEntity> products = orders.stream()
                .map(order -> {
                    Long productId = Long.valueOf(order.getItemId());
                    ProductInfoEntity product = productMap.get(productId);
                    if (product == null) {
                        throw new InvalidInputException(MessageEnum.PRODUCT_NOT_FOUND.withArgs(productId));
                    }
                    validateStock(product, order.getQuantity());
                    return product;
                }).toList();

        return new ProductValidationResult(products, calculateTotalPrice(orders, products));
    }

    private BigDecimal calculateTotalPrice(List<OrderInfoEntity> orders, List<ProductInfoEntity> products) {
        return IntStream.range(0, orders.size())
                .mapToObj(i -> products.get(i).getPrice().multiply(BigDecimal.valueOf(orders.get(i).getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateStock(ProductInfoEntity product, Integer requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            log.warn("Insufficient stock for product: {}. Available: {}, Requested: {}",
                    product.getTitle(), product.getStock(), requestedQuantity);
            throw new InvalidInputException(
                    MessageEnum.INSUFFICIENT_STOCK.withArgs(product.getTitle(), product.getStock()));
        }
    }

    private List<OrderInfoEntity> validateAndGetOrders(List<Long> orderIds, String userId) {
        List<OrderInfoEntity> orders = orderInfoService.getOrdersByIdsAndUserId(orderIds, userId);
        if (orders.size() != orderIds.size()) {
            log.error("Order mismatch. Found {}/{} orders for user {}", orders.size(), orderIds.size(), userId);
            throw new InvalidInputException(MessageEnum.ORDER_NOT_FOUND);
        }
        return orders;
    }

    private void validateTotalAmount(BigDecimal calculatedTotal, BigDecimal requestedTotal) {
        if (calculatedTotal.compareTo(requestedTotal) != 0) {
            log.error("Amount mismatch! Calculated: {}, Requested: {}", calculatedTotal, requestedTotal);
            throw new InvalidInputException(
                    MessageEnum.ORDER_AMOUNT_MISMATCH.withArgs(calculatedTotal, requestedTotal));
        }
    }

    private Long generateOrderCode() {
        return System.currentTimeMillis() * 1000 + ThreadLocalRandom.current().nextInt(1000);
    }

    private TransactionEntity buildTransactionEntity(CreateTransactionRequest request, Long orderCode) {
        return TransactionEntity.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .amount(request.getTotalAmount())
                .currency("VND")
                .status(TransactionEntity.Status.PENDING)
                .paymentMethod(TransactionEntity.PaymentMethod.PAYOS)
                .externalTxId(String.valueOf(orderCode))
                .description("Payment for Order #" + orderCode)
                .build();
    }
}
