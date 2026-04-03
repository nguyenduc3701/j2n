package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.service.TravelService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @GetMapping(value = "/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getCategoryById(@PathVariable Long id) {
        return travelService.getCategoryById(id);
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
    public Mono<Object> createCategory(@RequestBody Object input) {
        return travelService.createCategory(input);
    }

    @Operation(summary = "Update an existing category")
    @PutMapping(value = "/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateCategory(@PathVariable Long id, @RequestBody Object input) {
        return travelService.updateCategory(id, input);
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping(value = "/categories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteCategory(@PathVariable Long id) {
        return travelService.deleteCategory(id);
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
    @GetMapping(value = "/tours/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> getTourById(@PathVariable Long id) {
        return travelService.getTourById(id);
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
    public Mono<Object> createTour(@RequestBody Object input) {
        return travelService.createTour(input);
    }

    @Operation(summary = "Update an existing tour")
    @PutMapping(value = "/tours/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> updateTour(@PathVariable Long id, @RequestBody Object input) {
        return travelService.updateTour(id, input);
    }

    @Operation(summary = "Delete a tour")
    @DeleteMapping(value = "/tours/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 204, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public Mono<Object> deleteTour(@PathVariable Long id) {
        return travelService.deleteTour(id);
    }
}
