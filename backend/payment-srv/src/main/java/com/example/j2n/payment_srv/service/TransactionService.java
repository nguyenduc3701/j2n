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
import com.example.j2n.payment_srv.messaging.payment.event.PaymentConfirmedEvent;
import com.example.j2n.payment_srv.messaging.payment.publisher.PaymentEventPublisher;
import com.example.j2n.payment_srv.messaging.reservation.event.StockReservationEvent;
import com.example.j2n.payment_srv.messaging.reservation.publisher.ReservationPublisher;
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
    private final ReservationPublisher reservationPublisher;
    private final PaymentEventPublisher paymentEventPublisher;

    @LogAround(message = "Get all transactions")
    public BaseResponse<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionRepository.findAll().stream()
                .map(entity -> TransactionResponse.fromEntity(entity, null)) // Checkout URL is usually generated per
                                                                             // request
                .toList();
        return ResponseFactory.success(transactions);
    }

    @Transactional
    @LogAround(message = "Confirm transaction success")
    public void confirmTransactionSuccess(String externalTxId) {
        transactionRepository.findByExternalTxId(externalTxId).ifPresentOrElse(transaction -> {
            if (transaction.getStatus() != TransactionEntity.Status.PENDING) {
                log.error("[PAYMENT-SRV] Transaction {} is in status {} - cannot confirm as SUCCESS. Expected PENDING.",
                        externalTxId, transaction.getStatus());
                return;
            }
            finalizeTransactionAsSuccess(transaction);
            List<OrderInfoEntity> orders = orderInfoService.getOrdersByUserId(transaction.getUserId());
            StockReservationEvent stockEvent = buildStockReservationEvent(transaction, orders);
            confirmLocalStock(stockEvent);
            publishTransactionConfirmationEvents(transaction, orders, stockEvent);
            cleanupLocalOrders(transaction.getUserId());
            log.info("[PAYMENT-SRV] Transaction {} confirmation completed successfully.", externalTxId);
        }, () -> log.error("[PAYMENT-SRV] Transaction with external ID {} not found for confirmation.", externalTxId));
    }

    private void finalizeTransactionAsSuccess(TransactionEntity transaction) {
        transaction.setStatus(TransactionEntity.Status.SUCCESS);
        transactionRepository.save(transaction);
        log.debug("[PAYMENT-SRV] Transaction {} status updated to SUCCESS", transaction.getId());
    }

    private void confirmLocalStock(StockReservationEvent stockEvent) {
        productInfoService.confirmStock(stockEvent);
        log.debug("[PAYMENT-SRV] Local stock mirror confirmed for transaction {}", stockEvent.getTransactionId());
    }

    private void cleanupLocalOrders(String userId) {
        orderInfoService.deleteByUserId(userId);
        log.debug("[PAYMENT-SRV] Local order_info mirror cleared for user {}", userId);
    }

    private void publishTransactionConfirmationEvents(TransactionEntity transaction, List<OrderInfoEntity> orders,
            StockReservationEvent stockEvent) {
        log.info("[PAYMENT-SRV] Publishing confirmation events for transaction: {}", transaction.getId());
        reservationPublisher.publishConfirmStock(stockEvent);
        PaymentConfirmedEvent confirmedEvent = buildPaymentConfirmedEvent(transaction, orders);
        paymentEventPublisher.publishPaymentConfirmed(confirmedEvent);
    }

    @Transactional
    @LogAround(message = "Cancel pending transaction")
    public boolean cancelPendingTransaction(String transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    if (transaction.getStatus() == TransactionEntity.Status.PENDING) {
                        transaction.setStatus(TransactionEntity.Status.CANCELLED);
                        transactionRepository.save(transaction);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @LogAround(message = "Create a transaction")
    @Transactional
    public BaseResponse<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        log.info("Creating transaction for user: {}, amount: {}", request.getUserId(), request.getTotalAmount());
        List<OrderInfoEntity> orders = validateAndGetOrders(request.getOrderIds(), request.getUserId());
        ProductValidationResult validationResult = validateProductsAndPrepareItems(orders);
        validateTotalAmount(validationResult.price(), request.getTotalAmount());
        Long orderCode = generateOrderCode();
        CreatePaymentLinkResponse payOSResponse = payOSService.createPaymentLink(
                orderCode,
                validationResult.price(),
                orders,
                validationResult.products());
        TransactionEntity transaction = buildTransactionEntity(request, orderCode);
        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        handleStockReservation(savedTransaction, orders);

        return ResponseFactory
                .success(TransactionResponse.fromEntity(savedTransaction, payOSResponse.getCheckoutUrl()));
    }

    private record ProductValidationResult(List<ProductInfoEntity> products, BigDecimal price) {
    }

    private ProductValidationResult validateProductsAndPrepareItems(List<OrderInfoEntity> orders) {
        log.info("Validating products and preparing items for orders: {}", orders);
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
        log.info("Calculating total price for orders: {}, products: {}", orders, products);
        return IntStream.range(0, orders.size())
                .mapToObj(i -> products.get(i).getPrice().multiply(BigDecimal.valueOf(orders.get(i).getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateStock(ProductInfoEntity product, Integer requestedQuantity) {
        log.info("Validating stock for product: {}, requested quantity: {}", product, requestedQuantity);
        int availableStock = product.getStock() - product.getLockedStock();
        if (availableStock < requestedQuantity) {
            log.warn("Insufficient stock for product: {}. Available (incl. locked): {}, Requested: {}",
                    product.getTitle(), availableStock, requestedQuantity);
            throw new InvalidInputException(
                    MessageEnum.INSUFFICIENT_STOCK.withArgs(product.getTitle(), availableStock));
        }
    }

    private List<OrderInfoEntity> validateAndGetOrders(List<Long> orderIds, String userId) {
        log.info("Validating and getting orders for user: {}", userId);
        List<OrderInfoEntity> orders = orderInfoService.getOrdersByIdsAndUserId(orderIds, userId);
        if (orders.size() != orderIds.size()) {
            log.error("Order mismatch. Found {}/{} orders for user {}", orders.size(), orderIds.size(), userId);
            throw new InvalidInputException(MessageEnum.ORDER_NOT_FOUND);
        }
        return orders;
    }

    private void validateTotalAmount(BigDecimal calculatedTotal, BigDecimal requestedTotal) {
        log.info("Validating total amount for orders: {}, products: {}", calculatedTotal, requestedTotal);
        if (calculatedTotal.compareTo(requestedTotal) != 0) {
            log.error("Amount mismatch! Calculated: {}, Requested: {}", calculatedTotal, requestedTotal);
            throw new InvalidInputException(
                    MessageEnum.ORDER_AMOUNT_MISMATCH.withArgs(calculatedTotal, requestedTotal));
        }
    }

    private Long generateOrderCode() {
        log.info("Generating order code");
        return System.currentTimeMillis() * 1000 + ThreadLocalRandom.current().nextInt(1000);
    }

    private TransactionEntity buildTransactionEntity(CreateTransactionRequest request, Long orderCode) {
        log.info("Building transaction entity for order code: {}", orderCode);
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

    private void handleStockReservation(TransactionEntity transaction, List<OrderInfoEntity> orders) {
        log.info("Handling stock reservation for transaction: {}, orders: {}", transaction, orders);
        StockReservationEvent event = buildStockReservationEvent(transaction, orders);
        // 1. Lock stock locally in payment-srv (for validation)
        productInfoService.lockStock(event);
        // 2. Publish to product-srv to lock real stock
        reservationPublisher.publishLockStock(event);
        // 3. Publish to delay queue for auto-release
        reservationPublisher.publishReservationDelay(event);
    }

    private StockReservationEvent buildStockReservationEvent(TransactionEntity transaction,
            List<OrderInfoEntity> orders) {
        log.info("Building stock reservation event for transaction: {}, orders: {}", transaction, orders);
        return StockReservationEvent.builder()
                .transactionId(transaction.getId())
                .items(orders.stream()
                        .map(o -> StockReservationEvent.ProductStockItem.builder()
                                .productId(o.getItemId())
                                .quantity(o.getQuantity())
                                .build())
                        .toList())
                .build();
    }

    private PaymentConfirmedEvent buildPaymentConfirmedEvent(TransactionEntity transaction,
            List<OrderInfoEntity> orders) {
        log.info("Building payment confirmed event for transaction: {}", transaction.getId());
        return PaymentConfirmedEvent.builder()
                .transactionId(transaction.getId())
                .externalTxId(transaction.getExternalTxId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .items(orders.stream()
                        .map(o -> PaymentConfirmedEvent.OrderItem.builder()
                                .productId(o.getItemId())
                                .quantity(o.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}
