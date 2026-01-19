package com.example.j2n.image_srv.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import com.example.j2n.exception.ExternalServiceException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MinioFactoryTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioFactory minioFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upload_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        String bucketName = "test-bucket";

        String result = minioFactory.upload(file, bucketName);

        assertNotNull(result);
        assertTrue(result.contains("test.jpg"));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_Exception_ThrowsExternalServiceException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("Minio error"));

        assertThrows(ExternalServiceException.class, () -> minioFactory.upload(file, "bucket"));
    }

    @Test
    void getObject_Success() throws Exception {
        InputStream is = new ByteArrayInputStream("content".getBytes());
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(null); // Simple mock

        minioFactory.getObject("file", "bucket");

        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void getObject_Exception_ThrowsExternalServiceException() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("Minio error"));

        assertThrows(ExternalServiceException.class, () -> minioFactory.getObject("file", "bucket"));
    }

    @Test
    void removeObject_Success() throws Exception {
        minioFactory.removeObject("file", "bucket");

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void removeObject_Exception_ThrowsExternalServiceException() throws Exception {
        doThrow(new RuntimeException("Minio error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThrows(ExternalServiceException.class, () -> minioFactory.removeObject("file", "bucket"));
    }
}
