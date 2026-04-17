package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.TourImageRequest;
import com.example.j2n.bff_srv.controller.request.TourRequest;
import com.example.j2n.bff_srv.controller.request.TourScheduleRequest;
import com.example.j2n.bff_srv.service.TravelService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/api/bff/travel")
@RequiredArgsConstructor
@Tag(name = "Travel Management", description = "Endpoints for travel and tour management")
public class TravelController {

    private final TravelService travelService;


    @Operation(summary = "Get all categories")
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getAllCategories() {
        return travelService.getAllCategories();
    }

    @Operation(summary = "Get category by ID")
    @GetMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryById(@PathVariable Long categoryId) {
        return travelService.getCategoryById(categoryId);
    }

    @Operation(summary = "Get category by slug")
    @GetMapping(value = "/categories/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryBySlug(@PathVariable String slug) {
        return travelService.getCategoryBySlug(slug);
    }

    @Operation(summary = "Get category by name")
    @GetMapping(value = "/categories/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryByName(@PathVariable String name) {
        return travelService.getCategoryByName(name);
    }

    @Operation(summary = "Create a new category")
    @PostMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> createCategory(@Valid @RequestBody CategoryRequest input) {
        return travelService.createCategory(input);
    }

    @Operation(summary = "Update an existing category")
    @PutMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest input) {
        return travelService.updateCategory(categoryId, input);
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteCategory(@PathVariable Long categoryId) {
        return travelService.deleteCategory(categoryId);
    }

    // --- Tour Endpoints ---

    @Operation(summary = "Get all tours")
    @GetMapping(value = "/tours", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getAllTours() {
        return travelService.getAllTours();
    }

    @Operation(summary = "Get tour by ID")
    @GetMapping(value = "/tours/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getTourById(@PathVariable Long tourId) {
        return travelService.getTourById(tourId);
    }

    @Operation(summary = "Get tours by category ID")
    @GetMapping(value = "/tours/category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getToursByCategory(@PathVariable Long categoryId) {
        return travelService.getToursByCategory(categoryId);
    }

    @Operation(summary = "Create a new tour")
    @PostMapping(value = "/tours", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> createTour(@Valid @RequestBody TourRequest input) {
        return travelService.createTour(input);
    }

    @Operation(summary = "Update an existing tour")
    @PutMapping(value = "/tours/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateTour(@PathVariable Long tourId, @Valid @RequestBody TourRequest input) {
        return travelService.updateTour(tourId, input);
    }

    @Operation(summary = "Delete a tour")
    @DeleteMapping(value = "/tours/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteTour(@PathVariable Long tourId) {
        return travelService.deleteTour(tourId);
    }

    // --- Tour Image Endpoints ---

    @Operation(summary = "Get images by tour ID")
    @GetMapping(value = "/images/tour/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getImagesByTourId(@PathVariable Long tourId) {
        return travelService.getImagesByTourId(tourId);
    }

    @Operation(summary = "Add image to tour")
    @PostMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> addImageToTour(@Valid @RequestBody TourImageRequest input) {
        return travelService.addImageToTour(input);
    }

    @Operation(summary = "Delete tour image")
    @DeleteMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteTourImage(@PathVariable Long imageId) {
        return travelService.deleteTourImage(imageId);
    }

    @Operation(summary = "Set primary image for tour")
    @PatchMapping(value = "/images/{imageId}/primary", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> setPrimaryImage(@PathVariable Long imageId) {
        return travelService.setPrimaryImage(imageId);
    }

    // --- Tour Schedule Endpoints ---

    @Operation(summary = "Get schedules by tour ID")
    @GetMapping(value = "/schedules/tour/{tourId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getSchedulesByTourId(@PathVariable Long tourId) {
        return travelService.getSchedulesByTourId(tourId);
    }

    @Operation(summary = "Add schedule to tour")
    @PostMapping(value = "/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 201, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> addScheduleToTour(@Valid @RequestBody TourScheduleRequest input) {
        return travelService.addScheduleToTour(input);
    }

    @Operation(summary = "Update tour schedule")
    @PutMapping(value = "/schedules/{scheduleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateTourSchedule(@PathVariable Long scheduleId, @Valid @RequestBody TourScheduleRequest input) {
        return travelService.updateTourSchedule(scheduleId, input);
    }

    @Operation(summary = "Delete tour schedule")
    @DeleteMapping(value = "/schedules/{scheduleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteTourSchedule(@PathVariable Long scheduleId) {
        return travelService.deleteTourSchedule(scheduleId);
    }
}
