package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.UploadImageRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private RestClientUtil restClientUtil;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_Success() {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerId(1L)
                .ownerType(Optional.of("USER"))
                .build();

        when(restClientUtil.requestUpload(eq(GatewayPath.IMAGE_UPLOAD_PATH), any(), eq(Object.class)))
                .thenReturn(new Object());

        Object result = imageService.uploadImage(request);

        assertNotNull(result);
        verify(restClientUtil).requestUpload(eq(GatewayPath.IMAGE_UPLOAD_PATH), any(), eq(Object.class));
    }

    @Test
    void uploadImage_MissingFiles_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .files(null)
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void uploadImage_EmptyFiles_ThrowsException() {
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of())
                .ownerId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void uploadImage_MissingOwnerId_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "content".getBytes());
        UploadImageRequest request = UploadImageRequest.builder()
                .files(List.of(file))
                .ownerId(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> imageService.uploadImage(request));
    }

    @Test
    void getImageResource_Success() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok("content".getBytes());
        when(restClientUtil.requestBinary(anyString())).thenReturn(responseEntity);

        ResponseEntity<byte[]> result = imageService.getImageResource("USER", "1");

        assertNotNull(result);
        verify(restClientUtil).requestBinary(anyString());
    }

    @Test
    void downloadFile_Success() {
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok("content".getBytes());
        when(restClientUtil.requestBinary(anyString())).thenReturn(responseEntity);

        ResponseEntity<byte[]> result = imageService.downloadFile("2024");

        assertNotNull(result);
        verify(restClientUtil).requestBinary(anyString());
    }
}
