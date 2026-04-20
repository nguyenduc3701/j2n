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

        if (Boolean.TRUE.equals(entity.getIsPrimary())) {
            updateTourThumbnail(tour, entity.getImageUrl());
        }

        return ResponseFactory.success(tourImageRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete tour image")
    public BaseResponse<Boolean> deleteTourImage(Long id) {
        TourImageEntity entity = getTourImageByIdOrThrow(id);
        entity.setIsDeleted(true);
        tourImageRepository.save(entity);

        if (Boolean.TRUE.equals(entity.getIsPrimary())) {
            TourEntity tour = entity.getTour();
            // Try to find another primary image if available
            tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(tour.getId())
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(
                            img -> updateTourThumbnail(tour, img.getImageUrl()),
                            () -> updateTourThumbnail(tour, null)
                    );
        }
        return ResponseFactory.success(null);
    }

    @Transactional
    @LogAround(message = "Set primary image")
    public BaseResponse<TourImageEntity> setPrimaryImage(Long id) {
        TourImageEntity entity = getTourImageByIdOrThrow(id);
        if (!Boolean.TRUE.equals(entity.getIsPrimary())) {
            unmarkCurrentPrimary(entity.getTour().getId());
            entity.setIsPrimary(true);
            entity = tourImageRepository.save(entity);
            updateTourThumbnail(entity.getTour(), entity.getImageUrl());
        }
        return ResponseFactory.success(entity);
    }

    // --- Private helpers ---

    private TourImageEntity getTourImageByIdOrThrow(Long id) {
        ValidationUtils.validateLong(id);
        return tourImageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_IMAGE_NOT_FOUND));
    }

    private void updateTourThumbnail(TourEntity tour, String imageUrl) {
        log.info("Updating thumbnail for tour id: {}", tour.getId());
        if (imageUrl != null && !imageUrl.isEmpty()) {
            tour.setThumbnail(imageUrl);
            tourService.save(tour);
        }
    }

    private void unmarkCurrentPrimary(Long tourId) {
        log.info("Unmarking current primary images for tour id: {}", tourId);
        List<TourImageEntity> primaries = tourImageRepository.findAllByTourIdAndIsPrimaryTrueAndIsDeletedFalse(tourId);
        primaries.forEach(img -> img.setIsPrimary(false));
        tourImageRepository.saveAll(primaries);
    }

    @Transactional
    @LogAround(message = "Handle tour image upload event")
    public void handleTourImageUploadEvent(TourImageUploadEvent event) {
        TourEntity tour = tourService.getTourByIdOrThrow(event.getTourId());
        
        // Find the index of the first primary image in the event
        int primaryIndex = -1;
        for (int i = 0; i < event.getImages().size(); i++) {
            if (Boolean.TRUE.equals(event.getImages().get(i).getIsPrimary())) {
                primaryIndex = i;
                break;
            }
        }

        if (primaryIndex != -1) {
            unmarkCurrentPrimary(event.getTourId());
            updateTourThumbnail(tour, event.getImages().get(primaryIndex).getImageUrl());
        }

        final int finalPrimaryIndex = primaryIndex;
        List<TourImageEntity> entities = new java.util.ArrayList<>();
        for (int i = 0; i < event.getImages().size(); i++) {
            TourImageUploadEvent.ImageInfo img = event.getImages().get(i);
            if (!tourImageRepository.existsByTourIdAndImageUrlAndIsDeletedFalse(event.getTourId(), img.getImageUrl())) {
                entities.add(TourImageEntity.builder()
                        .tour(tour)
                        .imageUrl(img.getImageUrl())
                        .isPrimary(i == finalPrimaryIndex)
                        .isDeleted(false)
                        .build());
            }
        }

        if (!entities.isEmpty()) {
            tourImageRepository.saveAll(entities);
        }
    }
}
