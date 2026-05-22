package com.example.j2n.product_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.product_srv.dto.ProductImageDto;
import com.example.j2n.product_srv.messaging.product.event.ProductImageUploadEvent;
import com.example.j2n.product_srv.repository.ProductImageRepository;
import com.example.j2n.product_srv.repository.entity.ProductEntity;
import com.example.j2n.product_srv.repository.entity.ProductImageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductImageService productImageService;

    private ProductEntity mockProduct;
    private ProductImageEntity mockImage;
    private ProductImageDto mockDto;

    @BeforeEach
    void setUp() {
        mockProduct = ProductEntity.builder()
                .id(1L)
                .title("Product Test")
                .isDeleted(false)
                .build();

        mockImage = ProductImageEntity.builder()
                .id(1L)
                .product(mockProduct)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .isDeleted(false)
                .build();

        mockDto = ProductImageDto.builder()
                .productId(1L)
                .imageUrl("http://example.com/image.jpg")
                .isPrimary(false)
                .build();
    }

    @Test
    void getImagesByProductId_Success() {
        when(productImageRepository.findAllByProductIdAndIsDeletedFalse(1L)).thenReturn(Collections.singletonList(mockImage));

        BaseResponse<List<ProductImageEntity>> response = productImageService.getImagesByProductId(1L);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        verify(productImageRepository, times(1)).findAllByProductIdAndIsDeletedFalse(1L);
    }

    @Test
    void addImageToProduct_Success() {
        when(productService.getProductByIdOrThrow(1L)).thenReturn(mockProduct);
        when(productImageRepository.save(any(ProductImageEntity.class))).thenAnswer(i -> i.getArgument(0));

        BaseResponse<ProductImageEntity> response = productImageService.addImageToProduct(mockDto);

        assertNotNull(response.getData());
        verify(productService, times(1)).getProductByIdOrThrow(1L);
        verify(productImageRepository, times(1)).save(any(ProductImageEntity.class));
    }

    @Test
    void addImageToProduct_Success_AsPrimary() {
        mockDto.setIsPrimary(true);
        ProductImageEntity currentPrimary = ProductImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(productService.getProductByIdOrThrow(1L)).thenReturn(mockProduct);
        when(productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(List.of(currentPrimary));
        when(productImageRepository.save(any(ProductImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productImageRepository.saveAll(anyList())).thenReturn(List.of(currentPrimary));

        BaseResponse<ProductImageEntity> response = productImageService.addImageToProduct(mockDto);

        assertNotNull(response.getData());
        assertTrue(response.getData().getIsPrimary());
        verify(productImageRepository, times(1)).findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L);
        verify(productImageRepository, times(1)).saveAll(anyList());
        assertEquals("http://example.com/image.jpg", mockProduct.getThumbnail());
    }

    @Test
    void addImageToProduct_Fail_ProductNotFound() {
        when(productService.getProductByIdOrThrow(1L)).thenThrow(new DataNotFoundException(com.example.j2n.product_srv.constant.MessageEnum.PRODUCT_NOT_FOUND));

        assertThrows(DataNotFoundException.class, () -> productImageService.addImageToProduct(mockDto));
    }

    @Test
    void deleteProductImage_Success() {
        mockImage.setIsPrimary(true);
        when(productImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(productImageRepository.save(any(ProductImageEntity.class))).thenReturn(mockImage);
        when(productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Collections.emptyList());

        BaseResponse<Boolean> response = productImageService.deleteProductImage(1L);

        assertTrue(response.getData());
        assertTrue(mockImage.getIsDeleted());
        assertNull(mockProduct.getThumbnail());
        verify(productImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success() {
        when(productImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Collections.emptyList());
        when(productImageRepository.save(any(ProductImageEntity.class))).thenReturn(mockImage);

        BaseResponse<ProductImageEntity> response = productImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        assertEquals(mockImage.getImageUrl(), mockProduct.getThumbnail());
        verify(productImageRepository, times(1)).save(mockImage);
    }

    @Test
    void setPrimaryImage_Success_UnmarkOld() {
        ProductImageEntity currentPrimary = ProductImageEntity.builder().id(2L).isPrimary(true).build();
        
        when(productImageRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockImage));
        when(productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(List.of(currentPrimary));
        when(productImageRepository.save(any(ProductImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productImageRepository.saveAll(anyList())).thenReturn(List.of(currentPrimary));

        BaseResponse<ProductImageEntity> response = productImageService.setPrimaryImage(1L);

        assertNotNull(response.getData());
        assertTrue(mockImage.getIsPrimary());
        assertFalse(currentPrimary.getIsPrimary());
        verify(productImageRepository, times(1)).save(any(ProductImageEntity.class));
        verify(productImageRepository, times(1)).saveAll(anyList());
    }

    @Test
    void validateLong_Fail() {
        assertThrows(InvalidInputException.class, () -> productImageService.getImagesByProductId(null));
        assertThrows(InvalidInputException.class, () -> productImageService.getImagesByProductId(0L));
        assertThrows(InvalidInputException.class, () -> productImageService.getImagesByProductId(-1L));
    }

    @Test
    void handleProductImageUploadEvent_Success() {
        ProductImageUploadEvent.ImageInfo imgInfo = new ProductImageUploadEvent.ImageInfo("http://newimage.jpg", true);
        ProductImageUploadEvent event = ProductImageUploadEvent.builder()
                .productId(1L)
                .images(List.of(imgInfo))
                .build();

        when(productService.getProductByIdOrThrow(1L)).thenReturn(mockProduct);
        when(productImageRepository.findAllByProductIdAndIsPrimaryTrueAndIsDeletedFalse(1L)).thenReturn(Collections.emptyList());
        when(productImageRepository.existsByProductIdAndImageUrlAndIsDeletedFalse(1L, "http://newimage.jpg")).thenReturn(false);
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        productImageService.handleProductImageUploadEvent(event);

        assertEquals("http://newimage.jpg", mockProduct.getThumbnail());
        verify(productImageRepository, times(1)).saveAll(anyList());
        verify(productService, times(1)).save(mockProduct);
    }
}
