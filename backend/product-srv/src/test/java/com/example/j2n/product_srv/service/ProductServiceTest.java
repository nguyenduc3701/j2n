package com.example.j2n.product_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.product_srv.dto.ProductDto;
import com.example.j2n.product_srv.repository.ProductRepository;
import com.example.j2n.product_srv.repository.entity.CategoryEntity;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.messaging.product.event.ProductCreatedEvent;
import com.example.j2n.product_srv.messaging.product.event.ProductDeletedEvent;
import com.example.j2n.product_srv.messaging.product.publisher.ProductEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductEventPublisher productEventPublisher;

    @InjectMocks
    private ProductService productService;

    private CategoryEntity mockCategory;
    private ProductEntity mockProduct;
    private ProductDto mockDto;

    @BeforeEach
    void setUp() {
        mockCategory = CategoryEntity.builder()
                .id(1L)
                .name("Du lịch biển")
                .slug("du-lich-bien")
                .isDeleted(false)
                .build();

        mockProduct = ProductEntity.builder()
                .id(1L)
                .category(mockCategory)
                .title("Product Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang tuyệt vời")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .type("TOUR")
                .isDeleted(false)
                .build();

        mockDto = ProductDto.builder()
                .categoryId(1L)
                .title("Product Nha Trang 3N2Đ")
                .description("Khám phá Nha Trang tuyệt vời")
                .price(new BigDecimal("2500000.00"))
                .thumbnail("thumbnail.jpg")
                .duration("3 ngày 2 đêm")
                .startLocation("Hà Nội")
                .type("TOUR")
                .build();
    }

    // ===================== getAllProducts =====================

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAllByIsDeletedFalse()).thenReturn(Collections.singletonList(mockProduct));

        BaseResponse<List<ProductEntity>> response = productService.getAllProducts();

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(productRepository, times(1)).findAllByIsDeletedFalse();
    }

    // ===================== getProductById =====================

    @Test
    void getProductById_Success() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));

        BaseResponse<ProductEntity> response = productService.getProductById(1L);

        assertNotNull(response.getData());
        assertEquals(mockProduct.getTitle(), response.getData().getTitle());
        verify(productRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void getProductById_Fail_NotFound() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductById_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> productService.getProductById(null));
    }

    @Test
    void getProductById_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> productService.getProductById(0L));
    }

    @Test
    void getProductById_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> productService.getProductById(-5L));
    }

    // ===================== getProductsByCategory =====================

    @Test
    void getProductsByCategory_Success() {
        when(productRepository.findByCategoryIdAndIsDeletedFalse(1L)).thenReturn(Collections.singletonList(mockProduct));

        BaseResponse<List<ProductEntity>> response = productService.getProductsByCategory(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(productRepository, times(1)).findByCategoryIdAndIsDeletedFalse(1L);
    }

    @Test
    void getProductsByCategory_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> productService.getProductsByCategory(null));
    }

    @Test
    void getProductsByCategory_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> productService.getProductsByCategory(0L));
    }

    @Test
    void getProductsByCategory_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> productService.getProductsByCategory(-1L));
    }

    // ===================== getProductsByType =====================

    @Test
    void getProductsByType_Success() {
        when(productRepository.findByTypeAndIsDeletedFalse("TOUR")).thenReturn(Collections.singletonList(mockProduct));

        BaseResponse<List<ProductEntity>> response = productService.getProductsByType("TOUR");

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("TOUR", response.getData().get(0).getType());
        verify(productRepository, times(1)).findByTypeAndIsDeletedFalse("TOUR");
    }

    @Test
    void getProductsByType_Fail_InvalidInput_Null() {
        assertThrows(InvalidInputException.class, () -> productService.getProductsByType(null));
    }

    @Test
    void getProductsByType_Fail_InvalidInput_Empty() {
        assertThrows(InvalidInputException.class, () -> productService.getProductsByType(""));
        assertThrows(InvalidInputException.class, () -> productService.getProductsByType("   "));
    }

    // ===================== createProduct =====================

    @Test
    void createProduct_Success() {
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(mockCategory);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(mockProduct);

        BaseResponse<ProductEntity> response = productService.createProduct(mockDto);

        assertNotNull(response.getData());
        assertEquals(mockProduct.getTitle(), response.getData().getTitle());
        verify(categoryService, times(1)).getCategoryByIdOrThrow(1L);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
        verify(productEventPublisher, times(1)).publishProductCreated(any(ProductCreatedEvent.class));
    }

    @Test
    void createProduct_Fail_CategoryNotFound() {
        when(categoryService.getCategoryByIdOrThrow(1L)).thenThrow(new DataNotFoundException(com.example.j2n.product_srv.constant.MessageEnum.CATEGORY_NOT_FOUND));

        assertThrows(DataNotFoundException.class, () -> productService.createProduct(mockDto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_Fail_InvalidCategoryId_Null() {
        ProductDto dto = ProductDto.builder()
                .categoryId(null)
                .title("Product test")
                .price(new BigDecimal("1000000"))
                .build();

        when(categoryService.getCategoryByIdOrThrow(null)).thenThrow(new InvalidInputException(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST));

        assertThrows(InvalidInputException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_Fail_InvalidCategoryId_Zero() {
        ProductDto dto = ProductDto.builder()
                .categoryId(0L)
                .title("Product test")
                .price(new BigDecimal("1000000"))
                .build();

        when(categoryService.getCategoryByIdOrThrow(0L)).thenThrow(new InvalidInputException(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST));

        assertThrows(InvalidInputException.class, () -> productService.createProduct(dto));
    }

    // ===================== updateProduct =====================

    @Test
    void updateProduct_Success() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(mockCategory);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(mockProduct);

        BaseResponse<ProductEntity> response = productService.updateProduct(1L, mockDto);

        assertNotNull(response.getData());
        verify(productRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(categoryService, times(1)).getCategoryByIdOrThrow(1L);
        verify(productRepository, times(1)).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_Fail_ProductNotFound() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> productService.updateProduct(1L, mockDto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_Fail_ProductIsDeleted() {
        // findByIdAndIsDeletedFalse won't return deleted products, so simulate via not found
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> productService.updateProduct(1L, mockDto));
    }

    @Test
    void updateProduct_Fail_InvalidProductId_Null() {
        assertThrows(InvalidInputException.class, () -> productService.updateProduct(null, mockDto));
    }

    @Test
    void updateProduct_Fail_InvalidProductId_Zero() {
        assertThrows(InvalidInputException.class, () -> productService.updateProduct(0L, mockDto));
    }

    @Test
    void updateProduct_Fail_CategoryNotFound() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenThrow(new DataNotFoundException(com.example.j2n.product_srv.constant.MessageEnum.CATEGORY_NOT_FOUND));

        assertThrows(DataNotFoundException.class, () -> productService.updateProduct(1L, mockDto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_Fail_InvalidCategoryId_Null() {
        ProductDto dto = ProductDto.builder().categoryId(null).title("Product").price(new BigDecimal("1000000")).build();
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(categoryService.getCategoryByIdOrThrow(null)).thenThrow(new InvalidInputException(com.example.j2n.enums.BaseMessageEnum.BAD_REQUEST));

        assertThrows(InvalidInputException.class, () -> productService.updateProduct(1L, dto));
    }

    @Test
    void updateProduct_TypeIsNull_DoesNotUpdateType() {
        ProductDto dto = ProductDto.builder()
                .categoryId(1L)
                .title("Product Updated")
                .type(null) // Type is null
                .build();
        
        mockProduct.setType("EXISTING_TYPE");
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(categoryService.getCategoryByIdOrThrow(1L)).thenReturn(mockCategory);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BaseResponse<ProductEntity> response = productService.updateProduct(1L, dto);

        assertEquals("EXISTING_TYPE", response.getData().getType());
        verify(productRepository).save(mockProduct);
    }

    // ===================== deleteProduct =====================

    @Test
    void deleteProduct_Success() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(mockProduct);

        BaseResponse<Boolean> response = productService.deleteProduct(1L);

        assertTrue(response.getData());
        assertEquals("true", String.valueOf(mockProduct.getIsDeleted()));
        verify(productRepository, times(1)).save(any(ProductEntity.class));
        verify(productEventPublisher, times(1)).publishProductDeleted(any(ProductDeletedEvent.class));
    }

    @Test
    void deleteProduct_Fail_ProductNotFound() {
        when(productRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_Fail_InvalidId_Null() {
        assertThrows(InvalidInputException.class, () -> productService.deleteProduct(null));
    }

    @Test
    void deleteProduct_Fail_InvalidId_Zero() {
        assertThrows(InvalidInputException.class, () -> productService.deleteProduct(0L));
    }

    @Test
    void deleteProduct_Fail_InvalidId_Negative() {
        assertThrows(InvalidInputException.class, () -> productService.deleteProduct(-1L));
    }

    // ===================== validateProduct (isDeleted = true via entity mock) =====================

    @Test
    void updateProduct_Fail_ValidateProduct_IsDeleted_True() {
        ProductEntity deletedProduct = ProductEntity.builder()
                .id(2L)
                .category(mockCategory)
                .title("Product đã bị xóa")
                .price(new BigDecimal("1000000"))
                .isDeleted(true)
                .build();

        when(productRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(deletedProduct));

        assertThrows(DataNotFoundException.class, () -> productService.updateProduct(2L, mockDto));
    }

    @Test
    void deleteProduct_Fail_ValidateProduct_IsDeleted_True() {
        ProductEntity deletedProduct = ProductEntity.builder()
                .id(2L)
                .category(mockCategory)
                .title("Product đã bị xóa")
                .price(new BigDecimal("1000000"))
                .isDeleted(true)
                .build();

        when(productRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(deletedProduct));

        assertThrows(DataNotFoundException.class, () -> productService.deleteProduct(2L));
    }

    @Test
    void publishProductCreatedEvent_NullId_Skips() {
        ProductEntity product = new ProductEntity();
        product.setId(null);
        
        when(categoryService.getCategoryByIdOrThrow(anyLong())).thenReturn(mockCategory);
        when(productRepository.save(any())).thenReturn(product);
        
        productService.createProduct(mockDto);
        verify(productEventPublisher, never()).publishProductCreated(any());
    }

    @Test
    void publishProductDeletedEvent_NullId_Skips() {
        ProductEntity product = new ProductEntity();
        product.setId(null);
        when(productRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(product));
        
        productService.deleteProduct(1L);
        verify(productEventPublisher, never()).publishProductDeleted(any());
    }

    @Test
    void publishProductCreatedEvent_NullCreatedAt_UsesNull() {
        mockProduct.setCreatedAt(null);
        when(categoryService.getCategoryByIdOrThrow(anyLong())).thenReturn(mockCategory);
        when(productRepository.save(any())).thenReturn(mockProduct);

        productService.createProduct(mockDto);
        verify(productEventPublisher).publishProductCreated(argThat(event -> event.getCreatedAt() == null));
    }
}
