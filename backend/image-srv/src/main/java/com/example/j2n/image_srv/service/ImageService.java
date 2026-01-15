package com.example.j2n.image_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.image_srv.constant.BucketConstant;
import com.example.j2n.image_srv.constant.OwnerType;
import com.example.j2n.image_srv.dto.request.UploadImageRequest;
import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import com.example.j2n.image_srv.utils.MinioFactory;
import com.example.j2n.utils.ResponseFactory;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final static String FILE_PREFIX_NAME_STRING = "cv_nguyenminhduc_";
    private final ImageRepository imageRepository;
    private final MinioFactory minioFactory;

    public BaseResponse<List<ImageItemResponse>> uploadImage(UploadImageRequest request) {
        validateUploadImageRequest(request);
        validateOwnerType(request.getOwnerType().orElse(null));
        log.info("[ImageSrv] Start uploading images");
        List<ImageItemResponse> result = new ArrayList<>();
        for (MultipartFile file : request.getFiles()) {
            validateFileTypeSize(file);
            String fileName = minioFactory.upload(file,
                    mapOwnerTypeToBucketName(request.getOwnerType().orElse(OwnerType.DEFAULT)));
            ImageEntity imageEntity = buildAndSaveEntity(request, file, fileName);
            result.add(mapEntityToImageItemResponse(imageEntity));
        }
        log.info("[ImageSrv] End uploading images");
        return ResponseFactory.success(result);
    }

    public ResponseEntity<InputStreamResource> getImageResource(String ownerType, Long id) {
        log.info("[ImageSrv] Start getting image resource by id: {}", id);
        validateOwnerType(ownerType);
        ImageEntity imageEntity = validateImageId(id);
        InputStream stream = minioFactory.getObject(imageEntity.getFilePath(), mapOwnerTypeToBucketName(ownerType));
        log.info("[ImageSrv] End getting image resource by id: {}", id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageEntity.getContentType()))
                .body(new InputStreamResource(stream));
    }

    public ImageEntity validateImageId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Image id is null");
        }
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image id does not exist"));
    }

    private void validateUploadImageRequest(UploadImageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }
        if (request.getOwnerType() == null) {
            throw new IllegalArgumentException("Owner type is null");
        }
        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner id is null");
        }
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new IllegalArgumentException("Files is null or empty");
        }
    }

    private void validateOwnerType(String ownerType) {
        if (ownerType != null && !OwnerType.isValid(ownerType)) {
            throw new IllegalArgumentException("Invalid owner type");
        }
    }

    private String mapOwnerTypeToBucketName(String ownerType) {
        switch (ownerType.toUpperCase()) {
            case OwnerType.USER:
                return BucketConstant.USER_BUCKET;
            case OwnerType.PRODUCT:
                return BucketConstant.PRODUCT_BUCKET;
            case OwnerType.ROOM:
                return BucketConstant.ROOM_BUCKET;
            default:
                return BucketConstant.DEFAULT_BUCKET;
        }
    }

    private void validateFileTypeSize(MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }
        if (file.getSize() > 1024 * 1024 * 5) {
            throw new IllegalArgumentException("File size is too large");
        }
    }

    public ResponseEntity<InputStreamResource> getStaticFile(String year) {
        try {
            String downloadYear = year;
            if (year.equals("latest")) {
                downloadYear = Year.now().toString();
            }
            String fileName = String.format(FILE_PREFIX_NAME_STRING + downloadYear + ".pdf");
            ClassPathResource classPathResource = new ClassPathResource("static/" + fileName);
            InputStreamResource resource = new InputStreamResource(classPathResource.getInputStream());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(classPathResource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read static file", e);
        }
    }

    private ImageItemResponse mapEntityToImageItemResponse(ImageEntity imageEntity) {
        ImageItemResponse imageItemResponse = new ImageItemResponse();
        imageItemResponse.setId(imageEntity.getId());
        imageItemResponse.setFilePath(imageEntity.getFilePath());
        return imageItemResponse;
    }

    private ImageEntity buildAndSaveEntity(UploadImageRequest request, MultipartFile file, String fileName) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setOwnerType(request.getOwnerType().orElse(OwnerType.DEFAULT));
        imageEntity.setOwnerId(request.getOwnerId());
        imageEntity.setFileName(file.getOriginalFilename());
        imageEntity.setFilePath(fileName);
        imageEntity.setContentType(file.getContentType());
        imageEntity.setFileSize(file.getSize());
        imageEntity.setBucketName(mapOwnerTypeToBucketName(request.getOwnerType().orElse(OwnerType.DEFAULT)));
        imageEntity.setIsActive(true);
        return imageRepository.save(imageEntity);
    }
}