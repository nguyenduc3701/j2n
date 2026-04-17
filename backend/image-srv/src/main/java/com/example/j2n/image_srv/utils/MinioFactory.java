package com.example.j2n.image_srv.utils;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.ExternalServiceException;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioFactory {
    private final MinioClient minioClient;

    public String upload(MultipartFile file, String bucketName) {
        try {
            int randomLength = ThreadLocalRandom.current().nextInt(5, 11);
            String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, randomLength);
            String fileName = System.currentTimeMillis() + "_" + randomStr + "_" + file.getOriginalFilename()
                    .replace(" ", "_");
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
            throw new ExternalServiceException(BaseMessageEnum.EXTERNAL_SERVICE_ERROR, e);
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
            throw new ExternalServiceException(BaseMessageEnum.EXTERNAL_SERVICE_ERROR, e);
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
            throw new ExternalServiceException(BaseMessageEnum.EXTERNAL_SERVICE_ERROR, e);
        }
    }
}