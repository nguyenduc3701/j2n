package com.example.j2n.image_srv.service;

import com.example.j2n.image_srv.constant.BucketConstant;
import com.example.j2n.image_srv.constant.OwnerType;
import com.example.j2n.image_srv.dto.request.UploadImageRequest;
import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
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
                .file(file)
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .build();

        when(minioFactory.upload(any(), anyString())).thenReturn("generated_path");
        when(imageRepository.save(any(ImageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImageEntity result = imageService.uploadImage(request);

        assertNotNull(result);
        assertEquals("USER", result.getOwnerType());
        assertEquals(1L, result.getOwnerId());
        assertEquals("generated_path", result.getFilePath());
        assertEquals(BucketConstant.USER_BUCKET, result.getBucketName());
        verify(imageRepository, times(1)).save(any(ImageEntity.class));
    }

    @Test
    void uploadImage_InvalidOwnerType_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .ownerType(Optional.of("INVALID"))
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void uploadImage_NullOwnerType_UsesDefault() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .ownerType(Optional.empty())
                .ownerId(1L)
                .build();

        when(minioFactory.upload(any(), anyString())).thenReturn("path");
        when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ImageEntity result = imageService.uploadImage(request);

        assertEquals(OwnerType.DEFAULT, result.getOwnerType());
        assertEquals(BucketConstant.DEFAULT_BUCKET, result.getBucketName());
    }

    @Test
    void uploadImage_FileTypeNotImage_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
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
                .file(file)
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
                .file(new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes()))
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_NullOwnerType_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(null)
                .ownerId(1L)
                .file(new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes()))
                .build();
        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void validateUploadImageRequest_NullFile_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .ownerType(Optional.of("USER"))
                .ownerId(1L)
                .file(null)
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
                .file(file)
                .ownerType(Optional.of(type))
                .ownerId(1L)
                .build();
        when(minioFactory.upload(any(), eq(expectedBucket))).thenReturn("path");
        when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        ImageEntity result = imageService.uploadImage(request);
        assertEquals(expectedBucket, result.getBucketName());
    }

    @Test
    void getStaticFile_Success() {
        ResponseEntity<InputStreamResource> response = imageService.getStaticFile("2025");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getStaticFile_Latest_Success() {
        // "latest" uses Year.now() which matches cv_nguyenminhduc_2026.pdf in this
        // environment
        ResponseEntity<InputStreamResource> response = imageService.getStaticFile("latest");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getStaticFile_IOError_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> imageService.getStaticFile("nonexistent_year"));
    }
}
