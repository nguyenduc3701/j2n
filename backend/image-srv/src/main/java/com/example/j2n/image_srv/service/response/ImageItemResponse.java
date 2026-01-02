package com.example.j2n.image_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageItemResponse {
    private Long id;
    private String ownerType;
    private Long ownerId;
    private String fileName;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private String createdAt;
    private String updatedAt;
    private String url;
}
