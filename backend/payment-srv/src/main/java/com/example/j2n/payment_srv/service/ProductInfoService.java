package com.example.j2n.payment_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.payment_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.payment_srv.messaging.product.event.ProductUpdatedEvent;
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
                    entity.setUpdatedAt(LocalDateTime.now());
                    productInfoRepository.save(entity);
                });
    }

    public void syncProductDeleted(String productId) {
        productInfoRepository.deleteById(Long.valueOf(productId));
    }

    public ProductInfoEntity getProductInfo(Long id) {
        return productInfoRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException(MessageEnum.PRODUCT_NOT_FOUND.withArgs(id)));
    }

    public java.util.List<ProductInfoEntity> getProductInfos(java.util.List<Long> ids) {
        return productInfoRepository.findAllById(ids);
    }
}
