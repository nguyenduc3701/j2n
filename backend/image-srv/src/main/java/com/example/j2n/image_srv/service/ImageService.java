package com.example.j2n.image_srv.service;

import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.utils.MinioFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final MinioFactory minioFactory;

    public ImageEntity uploadImage(String ownerType, Long ownerId, String imageType, MultipartFile file) {
        log.info("[ImageSrv] Start uploading image: {}", file.getOriginalFilename());
        String fileName = minioFactory.upload(file);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setOwnerType(ownerType);
        imageEntity.setOwnerId(ownerId);
        imageEntity.setImageType(imageType);
        imageEntity.setFileName(file.getOriginalFilename());
        imageEntity.setFilePath(fileName);
        imageEntity.setContentType(file.getContentType());
        imageEntity.setFileSize(file.getSize());
        ImageEntity saved = imageRepository.save(imageEntity);
        log.info("[ImageSrv] End uploading image: {}", fileName);
        return saved;
    }

    public ResponseEntity<InputStreamResource> getImageResource(Long id) {
        log.info("[ImageSrv] Start getting image resource by id: {}", id);
        ImageEntity imageEntity = validateImageId(id);
        InputStream stream = minioFactory.getObject(imageEntity.getFilePath());
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
}
