package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.travel_srv.constant.MessageEnum;
import com.example.j2n.travel_srv.dto.TourImageDto;
import com.example.j2n.travel_srv.repository.TourImageRepository;
import com.example.j2n.travel_srv.repository.TourRepository;
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
    private final TourRepository tourRepository;

    @LogAround(message = "Get images by tour id")
    public BaseResponse<List<TourImageEntity>> getImagesByTourId(Long tourId) {
        validateLong(tourId);
        return ResponseFactory.success(tourImageRepository.findAllByTourIdAndIsDeletedFalse(tourId));
    }

    @Transactional
    @LogAround(message = "Add image to tour")
    public BaseResponse<TourImageEntity> addImageToTour(TourImageDto input) {
        TourEntity tour = getTourByIdOrThrow(input.getTourId());

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

    private TourEntity getTourByIdOrThrow(Long id) {
        validateLong(id);
        return tourRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_NOT_FOUND));
    }

    private TourImageEntity getTourImageByIdOrThrow(Long id) {
        validateLong(id);
        return tourImageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_IMAGE_NOT_FOUND));
    }

    private void unmarkCurrentPrimary(Long tourId) {
        tourImageRepository.findByTourIdAndIsPrimaryTrueAndIsDeletedFalse(tourId)
                .ifPresent(img -> {
                    img.setIsPrimary(false);
                    tourImageRepository.save(img);
                });
    }

    private void validateLong(Long value) {
        if (value == null || value <= 0) {
            throw new InvalidInputException(BaseMessageEnum.BAD_REQUEST);
        }
    }
}
