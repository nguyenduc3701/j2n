package com.example.j2n.payment_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.payment_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.payment_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.payment_srv.messaging.reservation.event.StockReservationEvent;
import com.example.j2n.payment_srv.repository.ProductInfoRepository;
import com.example.j2n.payment_srv.repository.entity.ProductInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.payment_srv.constant.MessageEnum;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductInfoService {

    private final ProductInfoRepository productInfoRepository;

    @Transactional
    @LogAround(message = "Sync Product Created")
    public void syncProductCreated(ProductCreatedEvent event) {
        ProductInfoEntity entity = ProductInfoEntity.builder()
                .id(Long.valueOf(event.getProductId()))
                .title(event.getTitle())
                .price(event.getPrice())
                .stock(event.getStock())
                .thumbnail(event.getThumbnail())
                .size(event.getSize())
                .design(event.getDesign())
                .isDeleted(event.getIsDeleted())
                .updatedAt(LocalDateTime.now())
                .build();
        productInfoRepository.save(entity);
    }

    @Transactional
    @LogAround(message = "Sync Product Updated")
    public void syncProductUpdated(ProductUpdatedEvent event) {
        productInfoRepository.findById(Long.valueOf(event.getProductId()))
                .ifPresent(entity -> {
                    entity.setTitle(event.getTitle());
                    entity.setPrice(event.getPrice());
                    entity.setStock(event.getStock());
                    entity.setThumbnail(event.getThumbnail());
                    entity.setSize(event.getSize());
                    entity.setDesign(event.getDesign());
                    entity.setIsDeleted(event.getIsDeleted());
                    entity.setUpdatedAt(LocalDateTime.now());
                    productInfoRepository.save(entity);
                });
    }

    @LogAround(message = "Sync Product Deleted")
    public void syncProductDeleted(String productId) {
        productInfoRepository.findById(Long.valueOf(productId))
                .ifPresent(entity -> {
                    entity.setIsDeleted(true);
                    productInfoRepository.save(entity);
                });
    }

    @LogAround(message = "Get Product Info")
    public ProductInfoEntity getProductInfo(Long id) {
        return productInfoRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException(MessageEnum.PRODUCT_NOT_FOUND.withArgs(id)));
    }

    @LogAround(message = "Get Product Infos")
    public java.util.List<ProductInfoEntity> getProductInfos(java.util.List<Long> ids) {
        return productInfoRepository.findAllById(ids);
    }

    @Transactional
    @LogAround(message = "Lock stock")
    public void lockStock(StockReservationEvent event) {
        event.getItems().forEach(item -> {
            productInfoRepository.findById(Long.valueOf(item.getProductId()))
                    .ifPresent(entity -> {
                        entity.setLockedStock(entity.getLockedStock() + item.getQuantity());
                        productInfoRepository.save(entity);
                    });
        });
    }

    @Transactional
    @LogAround(message = "Release stock")
    public void releaseStock(StockReservationEvent event) {
        event.getItems().forEach(item -> {
            productInfoRepository.findById(Long.valueOf(item.getProductId()))
                    .ifPresent(entity -> {
                        entity.setLockedStock(Math.max(0, entity.getLockedStock() - item.getQuantity()));
                        productInfoRepository.save(entity);
                    });
        });
    }

    @Transactional
    @LogAround(message = "Confirm stock")
    public void confirmStock(StockReservationEvent event) {
        event.getItems().forEach(item -> {
            productInfoRepository.findById(Long.valueOf(item.getProductId()))
                    .ifPresent(entity -> {
                        entity.setStock(entity.getStock() - item.getQuantity());
                        entity.setLockedStock(Math.max(0, entity.getLockedStock() - item.getQuantity()));
                        productInfoRepository.save(entity);
                    });
        });
    }
}
