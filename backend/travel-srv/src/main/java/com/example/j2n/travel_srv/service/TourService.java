package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.ValidationUtils;
import com.example.j2n.travel_srv.constant.MessageEnum;
import com.example.j2n.travel_srv.dto.TourDto;
import com.example.j2n.travel_srv.messaging.travel.event.TourCreatedEvent;
import com.example.j2n.travel_srv.messaging.travel.event.TourDeletedEvent;
import com.example.j2n.travel_srv.messaging.travel.publisher.TourEventPublisher;
import com.example.j2n.travel_srv.repository.TourRepository;
import com.example.j2n.travel_srv.repository.entity.CategoryEntity;
import com.example.j2n.travel_srv.repository.entity.TourEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final CategoryService categoryService;
    private final TourEventPublisher tourEventPublisher;

    @LogAround(message = "Get all tours")
    public BaseResponse<List<TourEntity>> getAllTours() {
        return ResponseFactory.success(tourRepository.findAllByIsDeletedFalse());
    }

    @LogAround(message = "Get tour by id")
    public BaseResponse<TourEntity> getTourById(Long id) {
        return ResponseFactory.success(getTourByIdOrThrow(id));
    }

    @LogAround(message = "Get tours by category")
    public BaseResponse<List<TourEntity>> getToursByCategory(Long categoryId) {
        ValidationUtils.validateLong(categoryId);
        return ResponseFactory.success(tourRepository.findByCategoryIdAndIsDeletedFalse(categoryId));
    }

    @Transactional
    @LogAround(message = "Create tour")
    public BaseResponse<TourEntity> createTour(TourDto input) {
        CategoryEntity category = categoryService.getCategoryByIdOrThrow(input.getCategoryId());

        TourEntity entity = TourEntity.builder()
                .category(category)
                .title(input.getTitle())
                .description(input.getDescription())
                .price(input.getPrice())
                .thumbnail(input.getThumbnail())
                .duration(input.getDuration())
                .startLocation(input.getStartLocation())
                .isDeleted(false)
                .build();

        TourEntity savedTour = tourRepository.save(entity);
        publishTourCreatedEvent(savedTour);
        return ResponseFactory.success(savedTour);
    }

    @Transactional
    @LogAround(message = "Update tour")
    public BaseResponse<TourEntity> updateTour(Long id, TourDto input) {
        TourEntity entity = getTourByIdOrThrow(id);
        validateTour(entity);

        CategoryEntity category = categoryService.getCategoryByIdOrThrow(input.getCategoryId());

        entity.setCategory(category);
        entity.setTitle(input.getTitle());
        entity.setDescription(input.getDescription());
        entity.setPrice(input.getPrice());
        entity.setThumbnail(input.getThumbnail());
        entity.setDuration(input.getDuration());
        entity.setStartLocation(input.getStartLocation());

        return ResponseFactory.success(tourRepository.save(entity));
    }

    @Transactional
    @LogAround(message = "Delete tour")
    public BaseResponse<Boolean> deleteTour(Long id) {
        TourEntity entity = getTourByIdOrThrow(id);
        validateTour(entity);
        entity.setIsDeleted(true);
        tourRepository.save(entity);
        publishTourDeletedEvent(entity);
        return ResponseFactory.success(null);
    }

    @Transactional
    public TourEntity save(TourEntity tour) {
        return tourRepository.save(tour);
    }

    // --- Private helpers ---

    public TourEntity getTourByIdOrThrow(Long id) {
        log.info("Get tour by id: {}", id);
        ValidationUtils.validateLong(id);
        return tourRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.TOUR_NOT_FOUND));
    }

    private void validateTour(TourEntity entity) {
        log.info("Validate tour: {}", entity.getId());
        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            log.error("Invalid input: tour is deleted");
            throw new DataNotFoundException(MessageEnum.TOUR_NOT_FOUND);
        }
    }

    private void publishTourCreatedEvent(TourEntity tour) {
        if (tour.getId() == null) {
            log.error("[TRAVEL-SRV] Tour id is null");
            return;
        }
        log.info("[TRAVEL-SRV] Publishing tour created event for tour: {}", tour.getTitle());
        TourCreatedEvent event = TourCreatedEvent.builder()
                .tourId(tour.getId().toString())
                .categoryId(tour.getCategory().getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .price(tour.getPrice())
                .thumbnail(tour.getThumbnail())
                .duration(tour.getDuration())
                .startLocation(tour.getStartLocation())
                .createdAt(tour.getCreatedAt() != null ? tour.getCreatedAt().toString() : null)
                .build();
        tourEventPublisher.publishTourCreated(event);
    }

    private void publishTourDeletedEvent(TourEntity tour) {
        if (tour.getId() == null) {
            log.error("[TRAVEL-SRV] Tour id is null");
            return;
        }
        log.info("[TRAVEL-SRV] Publishing tour deleted event for tour: {}", tour.getTitle());
        TourDeletedEvent event = TourDeletedEvent.builder()
                .tourId(tour.getId().toString())
                .build();
        tourEventPublisher.publishTourDeleted(event);
    }
}
