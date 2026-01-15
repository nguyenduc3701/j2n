package com.example.j2n.bff_srv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.UploadImageRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final RestClientUtil restClientUtil;

    public Object uploadImage(UploadImageRequest request) {
        log.info("[Start] Upload image request: {}", request);
        validateRequest(request);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile file : request.getFiles()) {
            body.add("files", file.getResource());
        }
        if (request.getOwnerId() != null) {
            body.add("ownerId", String.valueOf(request.getOwnerId()));
        }
        request.getOwnerType()
                .ifPresent(value -> body.add("ownerType", value));
        log.info("[End] Upload image request: {}", request);
        return restClientUtil.requestUpload(GatewayPath.IMAGE_UPLOAD_PATH, body, Object.class);
    }

    public ResponseEntity<byte[]> getImageResource(String ownerType, String id) {
        String path = String.format(GatewayPath.IMAGE_GET_IMAGE_PATH, ownerType, id);
        return restClientUtil.requestBinary(path);
    }

    public ResponseEntity<byte[]> downloadFile(String year) {
        String path = String.format(GatewayPath.IMAGE_DOWNLOAD_FILE_PATH, year);
        return restClientUtil.requestBinary(path);
    }

    private void validateRequest(UploadImageRequest request) {
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            log.error("Files is required");
            throw new IllegalArgumentException("Files is required");
        }
        if (request.getOwnerId() == null) {
            log.error("Owner id is required");
            throw new IllegalArgumentException("Owner id is required");
        }
    }
}
