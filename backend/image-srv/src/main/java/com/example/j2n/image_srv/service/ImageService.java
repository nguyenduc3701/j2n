package com.example.j2n.image_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.image_srv.constant.BucketConstant;
import com.example.j2n.image_srv.constant.OwnerType;
import com.example.j2n.image_srv.controller.request.UploadImageRequest;
import com.example.j2n.image_srv.exception.FileSizeException;
import com.example.j2n.image_srv.exception.FileTypeException;
import com.example.j2n.image_srv.exception.OwnerTypeException;
import com.example.j2n.image_srv.exception.StaticFileReadException;
import com.example.j2n.image_srv.messaging.user.event.UserAvatarUploadEvent;
import com.example.j2n.image_srv.messaging.user.publisher.UserEventPublisher;
import com.example.j2n.image_srv.repository.ImageRepository;
import com.example.j2n.image_srv.repository.entity.ImageEntity;
import com.example.j2n.image_srv.service.response.ImageItemResponse;
import com.example.j2n.image_srv.utils.MinioFactory;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.exception.DataNotFoundException;

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
    private final UserEventPublisher avatarEventPublisher;

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
        publishAvatarUploadedEvent(request.getFiles(), request.getOwnerId().toString(), result.get(0).getFilePath(),
                request.getOwnerType().orElse(OwnerType.DEFAULT), result.get(0).getId().toString());
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
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Image id"));
        }
        return imageRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(BaseMessageEnum.NOT_FOUND.withArgs("Image id")));
    }

    private void validateUploadImageRequest(UploadImageRequest request) {
        if (request == null) {
            throw new InvalidInputException(BaseMessageEnum.INVALID_REQUEST);
        }
        if (request.getOwnerType() == null) {
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Owner type"));
        }
        if (request.getOwnerId() == null) {
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Owner id"));
        }
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new InvalidInputException(BaseMessageEnum.FIELD_REQUIRED.withArgs("Files"));
        }
    }

    private void validateOwnerType(String ownerType) {
        if (ownerType != null && !OwnerType.isValid(ownerType)) {
            throw new OwnerTypeException();
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
            throw new FileTypeException();
        }
        if (file.getSize() > 1024 * 1024 * 5) {
            throw new FileSizeException();
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
            throw new StaticFileReadException(e);
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

    private void publishAvatarUploadedEvent(List<MultipartFile> files, String userId, String imageUrl, String ownerType,
            String imageId) {
        if (files.isEmpty() || files.size() > 1 || !ownerType.equals(OwnerType.USER) || imageId == null) {
            log.info("[ImageSrv] Skip publishing avatar uploaded event");
            return;
        }
        log.info("[ImageSrv] Publishing avatar uploaded event");
        UserAvatarUploadEvent event = new UserAvatarUploadEvent(userId, imageId, imageUrl);
        avatarEventPublisher.publishAvatarUploaded(event);
    }
}