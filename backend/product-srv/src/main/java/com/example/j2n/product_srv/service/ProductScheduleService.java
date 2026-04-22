package com.example.j2n.product_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.product_srv.constant.MessageEnum;
import com.example.j2n.product_srv.dto.ProductScheduleDto;
import com.example.j2n.product_srv.repository.ProductScheduleRepository;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.repository.entity.ProductScheduleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductScheduleService {

    private final ProductScheduleRepository productScheduleRepository;
    private final ProductService productService;

    @LogAround(message = "Get schedules by product id")
    public BaseResponse<List<ProductScheduleEntity>> getSchedulesByProductId(Long productId) {
        ValidationUtils.validateLong(productId);
        return ResponseFactory
                .success(productScheduleRepository.findAllByProductIdAndIsDeletedFalseOrderByDayNumberAsc(productId));
    }

    @Transactional
    @LogAround(message = "Add schedule to product")
    public BaseResponse<ProductScheduleEntity> addScheduleToProduct(ProductScheduleDto input) {
        ProductEntity product = productService.getProductByIdOrThrow(input.getProductId());

        ProductScheduleEntity entity = ProductScheduleEntity.builder()
                .product(product)
                .dayNumber(input.getDayNumber())
                .title(input.getTitle())
                .content(input.getContent())
                .isDeleted(false)
                .build();

        return ResponseFactory.of(MessageEnum.CREATE_PRODUCT_SCHEDULE_SUCCESS, productScheduleRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Update product schedule")
    public BaseResponse<ProductScheduleEntity> updateProductSchedule(Long id, ProductScheduleDto input) {
        ProductScheduleEntity entity = getProductScheduleByIdOrThrow(id);

        entity.setDayNumber(input.getDayNumber());
        entity.setTitle(input.getTitle());
        entity.setContent(input.getContent());

        return ResponseFactory.of(MessageEnum.UPDATE_PRODUCT_SCHEDULE_SUCCESS, productScheduleRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete product schedule")
    public BaseResponse<Boolean> deleteProductSchedule(Long id) {
        ProductScheduleEntity entity = getProductScheduleByIdOrThrow(id);
        entity.setIsDeleted(true);
        productScheduleRepository.save(entity);
        return ResponseFactory.of(MessageEnum.DELETE_PRODUCT_SCHEDULE_SUCCESS, true);
    }

    // --- Private helpers ---

    private ProductScheduleEntity getProductScheduleByIdOrThrow(Long id) {
        ValidationUtils.validateLong(id);
        return productScheduleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.PRODUCT_SCHEDULE_NOT_FOUND));
    }
}
