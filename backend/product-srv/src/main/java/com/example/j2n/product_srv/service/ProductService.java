package com.example.j2n.product_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.product_srv.constant.MessageEnum;
import com.example.j2n.product_srv.dto.ProductDto;
import com.example.j2n.product_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.product_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.product_srv.messaging.product.event.ProductUpdatedEvent;
import com.example.j2n.product_srv.messaging.product.publisher.ProductEventPublisher;
import com.example.j2n.product_srv.repository.ProductRepository;
import com.example.j2n.product_srv.repository.entity.CategoryEntity;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.service.response.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductEventPublisher productEventPublisher;

    @LogAround(message = "Get all products")
    public BaseResponse<List<ProductResponse>> getAllProducts() {
        return ResponseFactory.success(productRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @LogAround(message = "Get product by id")
    public BaseResponse<ProductResponse> getProductById(Long id) {
        return ResponseFactory.success(mapToResponse(getProductByIdOrThrow(id)));
    }

    @LogAround(message = "Get products by category")
    public BaseResponse<List<ProductResponse>> getProductsByCategory(Long categoryId) {
        ValidationUtils.validateLong(categoryId);
        return ResponseFactory.success(productRepository.findByCategoryIdAndIsDeletedFalse(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @LogAround(message = "Get products by type")
    public BaseResponse<List<ProductResponse>> getProductsByType(String type) {
        ValidationUtils.validateString(type);
        return ResponseFactory.success(productRepository.findByTypeAndIsDeletedFalse(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @Transactional
    @LogAround(message = "Create product")
    public BaseResponse<ProductResponse> createProduct(ProductDto input) {
        CategoryEntity category = categoryService.getCategoryByIdOrThrow(input.getCategoryId());

        ProductEntity entity = ProductEntity.builder()
                .category(category)
                .title(input.getTitle())
                .description(input.getDescription())
                .price(input.getPrice())
                .stock(input.getStock() != null ? input.getStock() : 0)
                .thumbnail(input.getThumbnail())
                .duration(input.getDuration())
                .startLocation(input.getStartLocation())
                .size(input.getSize())
                .design(input.getDesign())
                .type(input.getType())
                .isDeleted(false)
                .build();

        ProductEntity savedProduct = productRepository.save(entity);
        publishProductCreatedEvent(savedProduct);
        return ResponseFactory.of(MessageEnum.CREATE_PRODUCT_SUCCESS, mapToResponse(savedProduct));
    }

    @Transactional
    @LogAround(message = "Update product")
    public BaseResponse<ProductResponse> updateProduct(Long id, ProductDto input) {
        ProductEntity entity = getProductByIdOrThrow(id);
        validateProduct(entity);

        CategoryEntity category = categoryService.getCategoryByIdOrThrow(input.getCategoryId());

        entity.setCategory(category);
        entity.setTitle(input.getTitle());
        entity.setDescription(input.getDescription());
        entity.setPrice(input.getPrice());
        entity.setStock(input.getStock());
        entity.setThumbnail(input.getThumbnail());
        entity.setDuration(input.getDuration());
        entity.setStartLocation(input.getStartLocation());
        entity.setSize(input.getSize());
        entity.setDesign(input.getDesign());
        if (input.getType() != null) {
            entity.setType(input.getType());
        }

        ProductEntity savedProduct = productRepository.save(entity);
        publishProductUpdatedEvent(savedProduct);
        return ResponseFactory.of(MessageEnum.UPDATE_PRODUCT_SUCCESS, mapToResponse(savedProduct));
    }

    @Transactional
    @LogAround(message = "Delete product")
    public BaseResponse<Boolean> deleteProduct(Long id) {
        ProductEntity entity = getProductByIdOrThrow(id);
        validateProduct(entity);
        entity.setIsDeleted(true);
        productRepository.save(entity);
        publishProductDeletedEvent(entity);
        return ResponseFactory.of(MessageEnum.DELETE_PRODUCT_SUCCESS, true);
    }

    @Transactional
    @LogAround(message = "Save product")
    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }

    @Transactional
    @LogAround(message = "Lock stock")
    public void lockStock(Long productId, Integer quantity) {
        ProductEntity product = getProductByIdOrThrow(productId);
        product.setLockedStock(product.getLockedStock() + quantity);
        productRepository.save(product);
        publishProductUpdatedEvent(product);
    }

    @Transactional
    @LogAround(message = "Release stock")
    public void releaseStock(Long productId, Integer quantity) {
        ProductEntity product = getProductByIdOrThrow(productId);
        product.setLockedStock(Math.max(0, product.getLockedStock() - quantity));
        productRepository.save(product);
        publishProductUpdatedEvent(product);
    }

    @Transactional
    @LogAround(message = "Confirm stock")
    public void confirmStock(Long productId, Integer quantity) {
        ProductEntity product = getProductByIdOrThrow(productId);
        product.setStock(product.getStock() - quantity);
        product.setLockedStock(Math.max(0, product.getLockedStock() - quantity));
        productRepository.save(product);
        publishProductUpdatedEvent(product);
    }

    // --- Mapper ---

    public ProductResponse mapToResponse(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductResponse.CategoryInfo categoryInfo = null;
        if (entity.getCategory() != null) {
            categoryInfo = ProductResponse.CategoryInfo.builder()
                    .id(entity.getCategory().getId())
                    .name(entity.getCategory().getName())
                    .slug(entity.getCategory().getSlug())
                    .type(entity.getCategory().getType())
                    .isDeleted(entity.getCategory().getIsDeleted())
                    .build();
        }

        List<ProductResponse.ScheduleInfo> schedules = null;
        if (entity.getSchedules() != null) {
            schedules = entity.getSchedules().stream()
                    .filter(s -> !Boolean.TRUE.equals(s.getIsDeleted()))
                    .map(s -> ProductResponse.ScheduleInfo.builder()
                            .id(s.getId())
                            .dayNumber(s.getDayNumber())
                            .title(s.getTitle())
                            .content(s.getContent())
                            .hotel(s.getHotel())
                            .breakfast(s.getBreakfast())
                            .lunch(s.getLunch())
                            .dinner(s.getDinner())
                            .createdAt(s.getCreatedAt())
                            .updatedAt(s.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        List<ProductResponse.ImageInfo> images = null;
        if (entity.getImages() != null) {
            images = entity.getImages().stream()
                    .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                    .map(i -> ProductResponse.ImageInfo.builder()
                            .id(i.getId())
                            .imageUrl(i.getImageUrl())
                            .isPrimary(i.getIsPrimary())
                            .createdAt(i.getCreatedAt())
                            .updatedAt(i.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        return ProductResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .lockedStock(entity.getLockedStock())
                .thumbnail(entity.getThumbnail())
                .duration(entity.getDuration())
                .startLocation(entity.getStartLocation())
                .size(entity.getSize())
                .design(entity.getDesign())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .category(categoryInfo)
                .schedules(schedules)
                .images(images)
                .build();
    }

    // --- Private helpers ---

    public ProductEntity getProductByIdOrThrow(Long id) {
        log.info("Get product by id: {}", id);
        ValidationUtils.validateLong(id);
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.PRODUCT_NOT_FOUND));
    }

    private void validateProduct(ProductEntity entity) {
        log.info("Validate product: {}", entity.getId());
        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            log.error("Invalid input: product is deleted");
            throw new DataNotFoundException(MessageEnum.PRODUCT_NOT_FOUND);
        }
    }

    private void publishProductCreatedEvent(ProductEntity product) {
        if (product.getId() == null) {
            log.error("[PRODUCT-SRV] Product id is null");
            return;
        }
        log.info("[PRODUCT-SRV] Publishing product created event for product: {}", product.getTitle());
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(product.getId().toString())
                .categoryId(product.getCategory().getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .lockedStock(product.getLockedStock())
                .thumbnail(product.getThumbnail())
                .duration(product.getDuration())
                .startLocation(product.getStartLocation())
                .size(product.getSize())
                .design(product.getDesign())
                .type(product.getType())
                .isDeleted(product.getIsDeleted())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .build();
        productEventPublisher.publishProductCreated(event);
    }

    private void publishProductDeletedEvent(ProductEntity product) {
        if (product.getId() == null) {
            log.error("[PRODUCT-SRV] Product id is null");
            return;
        }
        log.info("[PRODUCT-SRV] Publishing product deleted event for product: {}", product.getTitle());
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId(product.getId().toString())
                .build();
        productEventPublisher.publishProductDeleted(event);
    }

    private void publishProductUpdatedEvent(ProductEntity product) {
        if (product.getId() == null) {
            log.error("[PRODUCT-SRV] Product id is null");
            return;
        }
        log.info("[PRODUCT-SRV] Publishing product updated event for product: {}", product.getTitle());
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId(product.getId().toString())
                .categoryId(product.getCategory().getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .lockedStock(product.getLockedStock())
                .thumbnail(product.getThumbnail())
                .duration(product.getDuration())
                .startLocation(product.getStartLocation())
                .size(product.getSize())
                .design(product.getDesign())
                .type(product.getType())
                .isDeleted(product.getIsDeleted())
                .updatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null)
                .build();
        productEventPublisher.publishProductUpdated(event);
    }
}
