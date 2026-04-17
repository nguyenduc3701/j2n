package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.TourImageRequest;
import com.example.j2n.bff_srv.controller.request.TourRequest;
import com.example.j2n.bff_srv.controller.request.TourScheduleRequest;
import com.example.j2n.bff_srv.utils.GraphQLFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

    @Mock
    private GraphQLFactory graphQLFactory;

    @InjectMocks
    private TravelService travelService;

    private static final String TRAVEL_CATEGORY_DOC = "travel-category";
    private static final String TRAVEL_TOUR_DOC = "travel-tour";

    // ===================== Category Methods =====================

    @Test
    void getAllCategories_ShouldReturnSuccess() {
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getAllCategories"), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getAllCategories();

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getAllCategories"), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryById_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryById"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryById(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryById"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryBySlug_ShouldReturnSuccess() {
        String slug = "test-slug";
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryBySlug"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryBySlug(slug);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryBySlug"), eq(Map.of("slug", slug)), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCategoryByName_ShouldReturnSuccess() {
        String name = "test-name";
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryByName"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getCategoryByName(name);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("getCategoryByName"), eq(Map.of("name", name)), any(ParameterizedTypeReference.class));
    }

    @Test
    void createCategory_ShouldReturnSuccess() {
        CategoryRequest input = CategoryRequest.builder().name("Test").slug("test").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("createCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.createCategory(input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("createCategory"), eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void updateCategory_ShouldReturnSuccess() {
        Long id = 1L;
        CategoryRequest input = CategoryRequest.builder().name("Test Updated").slug("test-updated").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("updateCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.updateCategory(id, input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("updateCategory"), eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteCategory_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_CATEGORY_DOC), eq("deleteCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.deleteCategory(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_CATEGORY_DOC), eq("deleteCategory"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    // ===================== Tour Methods =====================

    @Test
    void getAllTours_ShouldReturnSuccess() {
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("getAllTours"), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getAllTours();

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("getAllTours"), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void getTourById_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("getTourById"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getTourById(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("getTourById"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    @Test
    void getToursByCategory_ShouldReturnSuccess() {
        Long categoryId = 2L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("getToursByCategory"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getToursByCategory(categoryId);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("getToursByCategory"), eq(Map.of("categoryId", categoryId)), any(ParameterizedTypeReference.class));
    }

    @Test
    void createTour_ShouldReturnSuccess() {
        TourRequest input = TourRequest.builder().title("Tour").categoryId(1L).build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("createTour"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.createTour(input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("createTour"), eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void updateTour_ShouldReturnSuccess() {
        Long id = 1L;
        TourRequest input = TourRequest.builder().title("Tour Updated").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("updateTour"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.updateTour(id, input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("updateTour"), eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteTour_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq(TRAVEL_TOUR_DOC), eq("deleteTour"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.deleteTour(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq(TRAVEL_TOUR_DOC), eq("deleteTour"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    // ===================== Tour Image Methods =====================

    @Test
    void getImagesByTourId_ShouldReturnSuccess() {
        Long tourId = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-image"), eq("getImagesByTourId"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getImagesByTourId(tourId);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-image"), eq("getImagesByTourId"), eq(Map.of("tourId", tourId)), any(ParameterizedTypeReference.class));
    }

    @Test
    void addImageToTour_ShouldReturnSuccess() {
        TourImageRequest input = TourImageRequest.builder().tourId(1L).imageUrl("url").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-image"), eq("addImageToTour"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.addImageToTour(input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-image"), eq("addImageToTour"), eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteTourImage_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-image"), eq("deleteTourImage"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.deleteTourImage(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-image"), eq("deleteTourImage"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    @Test
    void setPrimaryImage_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-image"), eq("setPrimaryImage"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.setPrimaryImage(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-image"), eq("setPrimaryImage"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }

    // ===================== Tour Schedule Methods =====================

    @Test
    void getSchedulesByTourId_ShouldReturnSuccess() {
        Long tourId = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-schedule"), eq("getSchedulesByTourId"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.getSchedulesByTourId(tourId);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-schedule"), eq("getSchedulesByTourId"), eq(Map.of("tourId", tourId)), any(ParameterizedTypeReference.class));
    }

    @Test
    void addScheduleToTour_ShouldReturnSuccess() {
        TourScheduleRequest input = TourScheduleRequest.builder().tourId(1L).dayNumber(1).title("Day 1").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-schedule"), eq("addScheduleToTour"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.addScheduleToTour(input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-schedule"), eq("addScheduleToTour"), eq(Map.of("input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void updateTourSchedule_ShouldReturnSuccess() {
        Long id = 1L;
        TourScheduleRequest input = TourScheduleRequest.builder().title("Day 1 Updated").build();
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-schedule"), eq("updateTourSchedule"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.updateTourSchedule(id, input);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-schedule"), eq("updateTourSchedule"), eq(Map.of("id", id, "input", input)), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteTourSchedule_ShouldReturnSuccess() {
        Long id = 1L;
        Object expectedResponse = new Object();
        when(graphQLFactory.execute(eq("travel-schedule"), eq("deleteTourSchedule"), anyMap(), any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<Object> result = travelService.deleteTourSchedule(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(graphQLFactory).execute(eq("travel-schedule"), eq("deleteTourSchedule"), eq(Map.of("id", id)), any(ParameterizedTypeReference.class));
    }
}
