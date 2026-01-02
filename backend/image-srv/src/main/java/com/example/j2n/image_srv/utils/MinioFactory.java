package com.example.j2n.image_srv.utils;

import com.example.j2n.image_srv.config.MinioConfig;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioFactory {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String upload(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return fileName;
        } catch (Exception e) {
            log.error("[MinioFactory] Error uploading file to Minio: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to Minio", e);
        }
    }

    public InputStream getObject(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            log.error("[MinioFactory] Error getting file from Minio: {}", e.getMessage());
            throw new RuntimeException("Failed to get file from Minio", e);
        }
    }

    public void removeObject(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            log.error("[MinioFactory] Error removing file from Minio: {}", e.getMessage());
            throw new RuntimeException("Failed to remove file from Minio", e);
        }
    }
}