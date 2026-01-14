package com.example.j2n.image_srv.service;

import com.example.j2n.image_srv.constant.BucketConstant;
import com.example.j2n.image_srv.constant.OwnerType;
import com.example.j2n.image_srv.dto.request.UploadImageRequest;
import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.utils.MinioFactory;
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

import org.springframework.http.HttpHeaders;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final static String FILE_PREFIX_NAME_STRING = "cv_nguyenminhduc_";
    private final ImageRepository imageRepository;
    private final MinioFactory minioFactory;

    public ImageEntity uploadImage(UploadImageRequest request) {
        validateUploadImageRequest(request);
        validateFileTypeSize(request.getFile());
        log.info("[ImageSrv] Start uploading image: {}", request.getFile().getOriginalFilename());
        validateOwnerType(request.getOwnerType().orElse(null));
        String fileName = minioFactory.upload(request.getFile(),
                mapOwnerTypeToBucketName(request.getOwnerType().orElse(OwnerType.DEFAULT)));
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setOwnerType(request.getOwnerType().orElse(OwnerType.DEFAULT));
        imageEntity.setOwnerId(request.getOwnerId());
        imageEntity.setFileName(request.getFile().getOriginalFilename());
        imageEntity.setFilePath(fileName);
        imageEntity.setContentType(request.getFile().getContentType());
        imageEntity.setFileSize(request.getFile().getSize());
        imageEntity.setBucketName(mapOwnerTypeToBucketName(request.getOwnerType().orElse(OwnerType.DEFAULT)));
        imageEntity.setIsActive(true);
        ImageEntity saved = imageRepository.save(imageEntity);
        log.info("[ImageSrv] End uploading image: {}", fileName);
        return saved;
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
        if (request.getFile() == null) {
            throw new IllegalArgumentException("File is null");
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
}
