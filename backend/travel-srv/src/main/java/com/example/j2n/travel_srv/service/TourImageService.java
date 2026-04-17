package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.travel_srv.constant.MessageEnum;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.messaging.travel.event.TourImageUploadEvent;
import com.example.j2n.travel_srv.repository.TourImageRepository;
import com.example.j2n.travel_srv.repository.entity.TourEntity;
import com.example.j2n.travel_srv.repository.entity.TourImageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourImageService {

    private final TourImageRepository tourImageRepository;
    private final TourService tourService;

    @LogAround(message = "Get images by tour id")
    public BaseResponse<List<TourImageEntity>> getImagesByTourId(Long tourId) {
        ValidationUtils.validateLong(tourId);
        return ResponseFactory.success(tourImageRepository.findAllByTourIdAndIsDeletedFalse(tourId));
    }

    @Transactional
    @LogAround(message = "Add image to tour")
    public BaseResponse<TourImageEntity> addImageToTour(TourImageDto input) {
        TourEntity tour = tourService.getTourByIdOrThrow(input.getTourId());
        if (Boolean.TRUE.equals(input.getIsPrimary())) {
            unmarkCurrentPrimary(input.getTourId());
        }
        TourImageEntity entity = TourImageEntity.builder()
                .tour(tour)
                .imageUrl(input.getImageUrl())
                .isPrimary(Boolean.TRUE.equals(input.getIsPrimary()))
                .isDeleted(false)
                .build();
        return ResponseFactory.success(tourImageRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete tour image")
    public BaseResponse<Boolean> deleteTourImage(Long id) {
        TourImageEntity entity = getTourImageByIdOrThrow(id);
        entity.setIsDeleted(true);
        tourImageRepository.save(entity);
        return ResponseFactory.success(true);
    }

    @Transactional
    @LogAround(message = "Set primary image")
    public BaseResponse<TourImageEntity> setPrimaryImage(Long id) {
        TourImageEntity entity = getTourImageByIdOrThrow(id);
        if (!Boolean.TRUE.equals(entity.getIsPrimary())) {
            unmarkCurrentPrimary(entity.getTour().getId());
            entity.setIsPrimary(true);
            entity = tourImageRepository.save(entity);
        }
        return ResponseFactory.success(entity);
    }

    // --- Private helpers ---

    private TourImageEntity getTourImageByIdOrThrow(Long id) {
        ValidationUtils.validateLong(id);
        return tourImageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_IMAGE_NOT_FOUND));
    }

    private void unmarkCurrentPrimary(Long tourId) {
        log.info("Unmarking current primary image for tour id: {}", tourId);
        tourImageRepository.findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(tourId)
                .ifPresent(img -> {
                    img.setIsPrimary(false);
                    tourImageRepository.save(img);
                });
    }

    @Transactional
    @LogAround(message = "Handle tour image upload event")
    public void handleTourImageUploadEvent(TourImageUploadEvent event) {
        TourEntity tour = tourService.getTourByIdOrThrow(event.getTourId());
        boolean hasNewPrimary = event.getImages().stream()
                .anyMatch(img -> Boolean.TRUE.equals(img.getIsPrimary()));
        if (hasNewPrimary) {
            unmarkCurrentPrimary(event.getTourId());
        }
        List<TourImageEntity> entities = event.getImages().stream()
                .map(img -> TourImageEntity.builder()
                        .tour(tour)
                        .imageUrl(img.getImageUrl())
                        .isPrimary(Boolean.TRUE.equals(img.getIsPrimary()))
                        .isDeleted(false)
                        .build())
                .toList();
        tourImageRepository.saveAll(entities);
    }
}
