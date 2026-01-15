package com.example.j2n.image_srv.utils;

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

    public String upload(MultipartFile file, String bucketName) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ", "_");
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
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

    public InputStream getObject(String fileName, String bucketName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            log.error("[MinioFactory] Error getting file from Minio: {}", e.getMessage());
            throw new RuntimeException("Failed to get file from Minio", e);
        }
    }

    public void removeObject(String fileName, String bucketName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            log.error("[MinioFactory] Error removing file from Minio: {}", e.getMessage());
            throw new RuntimeException("Failed to remove file from Minio", e);
        }
    }
}