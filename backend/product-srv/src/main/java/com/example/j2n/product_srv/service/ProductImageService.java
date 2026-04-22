package com.example.j2n.product_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.product_srv.constant.MessageEnum;
import com.example.j2n.product_srv.dto.ProductImageDto;
import com.example.j2n.product_srv.messaging.product.event.ProductImageUploadEvent;
import com.example.j2n.product_srv.repository.ProductImageRepository;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.repository.entity.ProductImageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;

    @LogAround(message = "Get images by product id")
    public BaseResponse<List<ProductImageEntity>> getImagesByProductId(Long productId) {
        ValidationUtils.validateLong(productId);
        return ResponseFactory.success(productImageRepository.findAllByProductIdAndIsDeletedFalse(productId));
    }

    @Transactional
    @LogAround(message = "Add image to product")
    public BaseResponse<ProductImageEntity> addImageToProduct(ProductImageDto input) {
        ProductEntity product = productService.getProductByIdOrThrow(input.getProductId());
        if (Boolean.TRUE.equals(input.getIsPrimary())) {
            unmarkCurrentPrimary(input.getProductId());
        }
        ProductImageEntity entity = ProductImageEntity.builder()
                .product(product)
                .imageUrl(input.getImageUrl())
                .isPrimary(Boolean.TRUE.equals(input.getIsPrimary()))
                .isDeleted(false)
                .build();

        if (Boolean.TRUE.equals(entity.getIsPrimary())) {
            updateProductThumbnail(product, entity.getImageUrl());
        }

        return ResponseFactory.of(MessageEnum.CREATE_PRODUCT_IMAGE_SUCCESS, productImageRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete product image")
    public BaseResponse<Boolean> deleteProductImage(Long id) {
        ProductImageEntity entity = getProductImageByIdOrThrow(id);
        entity.setIsDeleted(true);
        productImageRepository.save(entity);

        if (Boolean.TRUE.equals(entity.getIsPrimary())) {
            ProductEntity product = entity.getProduct();
            // Try to find another primary image if available
            productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(product.getId())
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(
                            img -> updateProductThumbnail(product, img.getImageUrl()),
                            () -> updateProductThumbnail(product, null)
                    );
        }
        return ResponseFactory.of(MessageEnum.DELETE_PRODUCT_IMAGE_SUCCESS, true);
    }

    @Transactional
    @LogAround(message = "Set primary image")
    public BaseResponse<ProductImageEntity> setPrimaryImage(Long id) {
        ProductImageEntity entity = getProductImageByIdOrThrow(id);
        if (!Boolean.TRUE.equals(entity.getIsPrimary())) {
            unmarkCurrentPrimary(entity.getProduct().getId());
            entity.setIsPrimary(true);
            entity = productImageRepository.save(entity);
            updateProductThumbnail(entity.getProduct(), entity.getImageUrl());
        }
        return ResponseFactory.of(MessageEnum.UPDATE_PRODUCT_IMAGE_SUCCESS, entity);
    }

    // --- Private helpers ---

    private ProductImageEntity getProductImageByIdOrThrow(Long id) {
        ValidationUtils.validateLong(id);
        return productImageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.PRODUCT_IMAGE_NOT_FOUND));
    }

    private void updateProductThumbnail(ProductEntity product, String imageUrl) {
        log.info("Updating thumbnail for product id: {}", product.getId());
        if (imageUrl != null && !imageUrl.isEmpty()) {
            product.setThumbnail(imageUrl);
            productService.save(product);
        }
    }

    private void unmarkCurrentPrimary(Long productId) {
        log.info("Unmarking current primary images for product id: {}", productId);
        List<ProductImageEntity> primaries = productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(productId);
        primaries.forEach(img -> img.setIsPrimary(false));
        productImageRepository.saveAll(primaries);
    }

    @Transactional
    @LogAround(message = "Handle product image upload event")
    public void handleProductImageUploadEvent(ProductImageUploadEvent event) {
        ProductEntity product = productService.getProductByIdOrThrow(event.getProductId());
        
        // Find the index of the first primary image in the event
        int primaryIndex = -1;
        for (int i = 0; i < event.getImages().size(); i++) {
            if (Boolean.TRUE.equals(event.getImages().get(i).getIsPrimary())) {
                primaryIndex = i;
                break;
            }
        }

        if (primaryIndex != -1) {
            unmarkCurrentPrimary(event.getProductId());
            updateProductThumbnail(product, event.getImages().get(primaryIndex).getImageUrl());
        }

        final int finalPrimaryIndex = primaryIndex;
        List<ProductImageEntity> entities = new java.util.ArrayList<>();
        for (int i = 0; i < event.getImages().size(); i++) {
            ProductImageUploadEvent.ImageInfo img = event.getImages().get(i);
            if (!productImageRepository.existsByProductIdAndImageUrlAndIsDeletedFalse(event.getProductId(), img.getImageUrl())) {
                entities.add(ProductImageEntity.builder()
                        .product(product)
                        .imageUrl(img.getImageUrl())
                        .isPrimary(i == finalPrimaryIndex)
                        .isDeleted(false)
                        .build());
            }
        }

        if (!entities.isEmpty()) {
            productImageRepository.saveAll(entities);
        }
    }
}
