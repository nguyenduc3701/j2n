package com.example.j2n.image_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.image_srv.constant.BucketConstant;
import com.example.j2n.image_srv.constant.OwnerType;
import com.example.j2n.image_srv.controller.request.UploadImageRequest;
import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import com.example.j2n.image_srv.utils.MinioFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MinioFactory minioFactory;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .build();

        when(minioFactory.upload(any(), anyString())).thenReturn("generated_path");
        when(imageRepository.save(any(ImageEntity.class))).thenAnswer(invocation -> {
            ImageEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        BaseResponse<List<ImageItemResponse>> result = imageService.uploadImage(request);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        ImageItemResponse response = result.getData().get(0);
        assertEquals(1L, response.getId());
        assertEquals("generated_path", response.getFilePath());
        verify(imageRepository, times(1)).save(any(ImageEntity.class));
        verify(minioFactory).upload(any(), eq(BucketConstant.USER_BUCKET));
    }

    @Test
    void uploadImage_MultiFile_Success() {
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.jpg", "image/jpeg", "content2".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file1, file2))
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .build();

        when(minioFactory.upload(any(), anyString())).thenReturn("path1", "path2");
        when(imageRepository.save(any(ImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BaseResponse<List<ImageItemResponse>> result = imageService.uploadImage(request);

        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals("path1", result.getData().get(0).getFilePath());
        assertEquals("path2", result.getData().get(1).getFilePath());
        verify(imageRepository, times(2)).save(any(ImageEntity.class));
    }

    @Test
    void uploadImage_InvalidOwnerType_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.of("INVALID"))
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void uploadImage_NullOwnerType_UsesDefault() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.empty())
                .ownerId(1L)
                .build();

        when(minioFactory.upload(any(), anyString())).thenReturn("path");
        when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BaseResponse<List<ImageItemResponse>> result = imageService.uploadImage(request);

        assertEquals("path", result.getData().get(0).getFilePath());
        verify(minioFactory).upload(any(), eq(BucketConstant.DEFAULT_BUCKET));
    }

    @Test
    void uploadImage_FileTypeNotImage_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void uploadImage_FileSizeTooLarge_ThrowsException() {
        byte[] largeContent = new byte[1024 * 1024 * 6]; // 6MB
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeContent);
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_NullRequest_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(null));
    }

    @Test
    void validateUploadImageRequest_NullOwnerId_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(Optional.of("USER"))
                .ownerId(null)
                .files(List.of(new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes())))
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_NullOwnerType_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(null)
                .ownerId(1L)
                .files(List.of(new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes())))
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_NullFiles_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .files(null)
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_EmptyFiles_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .files(List.of())
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void getImageResource_Success() {
        ImageEntity entity = new ImageEntity();
        entity.setFilePath("path");
        entity.setContentType("image/jpeg");
        when(imageRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(minioFactory.getObject(anyString(), anyString())).thenReturn(new ByteArrayInputStream("data".getBytes()));

        ResponseEntity<InputStreamResource> response = imageService.getImageResource("USER", 1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getHeaders().getContentType());
        assertEquals("image/jpeg", response.getHeaders().getContentType().toString());
    }

    @Test
    void validateImageId_NotFound_ThrowsException() {
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> imageService.getImageResource("USER", 1L));
    }

    @Test
    void validateImageId_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> imageService.validateImageId(null));
    }

    @Test
    void mapOwnerTypeToBucketName_AllTypes() {
        testBucketMapping("USER", BucketConstant.USER_BUCKET);
        testBucketMapping("PRODUCT", BucketConstant.PRODUCT_BUCKET);
        testBucketMapping("ROOM", BucketConstant.ROOM_BUCKET);
    }

    private void testBucketMapping(String type, String expectedBucket) {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerType(Optional.of(type))
                .ownerId(1L)
                .build();
        when(minioFactory.upload(any(), eq(expectedBucket))).thenReturn("path");
        when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        imageService.uploadImage(request);
        verify(minioFactory).upload(any(), eq(expectedBucket));
    }

    @Test
    void getStaticFile_Success() {
        ResponseEntity<InputStreamResource> response = imageService.getStaticFile("2025");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getStaticFile_Latest_Success() {
        ResponseEntity<InputStreamResource> response = imageService.getStaticFile("latest");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getStaticFile_IOError_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> imageService.getStaticFile("nonexistent_year"));
    }
}
