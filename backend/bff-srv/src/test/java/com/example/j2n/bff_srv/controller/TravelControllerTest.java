package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.TourImageRequest;
import com.example.j2n.bff_srv.controller.request.TourRequest;
import com.example.j2n.bff_srv.controller.request.TourScheduleRequest;
import com.example.j2n.bff_srv.service.TravelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(TravelController.class)
class TravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TravelService travelService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CATEGORY_BASE_URL = "/api/bff/travel/categories";
    private static final String TOUR_BASE_URL = "/api/bff/travel/tours";

    // ===================== Category Endpoints =====================

    @Test
    void getAllCategories_ShouldReturnSuccess() throws Exception {
        Object expectedResponse = Collections.singletonMap("data", "list");
        when(travelService.getAllCategories()).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(CATEGORY_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryById_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "item");
        when(travelService.getCategoryById(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(CATEGORY_BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryBySlug_ShouldReturnSuccess() throws Exception {
        String slug = "test-slug";
        Object expectedResponse = Collections.singletonMap("data", "item-by-slug");
        when(travelService.getCategoryBySlug(slug)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(CATEGORY_BASE_URL + "/slug/{slug}", slug)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryByName_ShouldReturnSuccess() throws Exception {
        String name = "test-name";
        Object expectedResponse = Collections.singletonMap("data", "item-by-name");
        when(travelService.getCategoryByName(name)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(CATEGORY_BASE_URL + "/name/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createCategory_ShouldReturnSuccess() throws Exception {
        CategoryRequest input = CategoryRequest.builder().name("New Category").slug("new-category").build();
        Object expectedResponse = Collections.singletonMap("data", "created");
        when(travelService.createCategory(any(CategoryRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post(CATEGORY_BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCategory_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        CategoryRequest input = CategoryRequest.builder().name("Updated Category").slug("updated-category").build();
        Object expectedResponse = Collections.singletonMap("data", "updated");
        when(travelService.updateCategory(eq(id), any(CategoryRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(put(CATEGORY_BASE_URL + "/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "deleted");
        when(travelService.deleteCategory(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(delete(CATEGORY_BASE_URL + "/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ===================== Tour Endpoints =====================

    @Test
    void getAllTours_ShouldReturnSuccess() throws Exception {
        Object expectedResponse = Collections.singletonMap("data", "tour-list");
        when(travelService.getAllTours()).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(TOUR_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getTourById_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "tour-item");
        when(travelService.getTourById(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(TOUR_BASE_URL + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getToursByCategory_ShouldReturnSuccess() throws Exception {
        Long categoryId = 2L;
        Object expectedResponse = Collections.singletonMap("data", "tours-by-category");
        when(travelService.getToursByCategory(categoryId)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get(TOUR_BASE_URL + "/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createTour_ShouldReturnSuccess() throws Exception {
        TourRequest input = TourRequest.builder().title("Tour Nha Trang").categoryId(1L).price(java.math.BigDecimal.valueOf(100)).build();
        Object expectedResponse = Collections.singletonMap("data", "tour-created");
        when(travelService.createTour(any(TourRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post(TOUR_BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTour_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        TourRequest input = TourRequest.builder().title("Tour Đà Nẵng Updated").build();
        Object expectedResponse = Collections.singletonMap("data", "tour-updated");
        when(travelService.updateTour(eq(id), any(TourRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(put(TOUR_BASE_URL + "/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTour_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "tour-deleted");
        when(travelService.deleteTour(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(delete(TOUR_BASE_URL + "/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ===================== Tour Image Endpoints =====================

    @Test
    void getImagesByTourId_ShouldReturnSuccess() throws Exception {
        Long tourId = 1L;
        Object expectedResponse = Collections.singletonMap("data", "image-list");
        when(travelService.getImagesByTourId(tourId)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get("/api/bff/travel/images/tour/{tourId}", tourId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addImageToTour_ShouldReturnSuccess() throws Exception {
        TourImageRequest input = TourImageRequest.builder().tourId(1L).imageUrl("http://image.com").build();
        Object expectedResponse = Collections.singletonMap("data", "image-added");
        when(travelService.addImageToTour(any(TourImageRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post("/api/bff/travel/images")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTourImage_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "image-deleted");
        when(travelService.deleteTourImage(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(delete("/api/bff/travel/images/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void setPrimaryImage_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "image-primary-set");
        when(travelService.setPrimaryImage(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(patch("/api/bff/travel/images/{id}/primary", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ===================== Tour Schedule Endpoints =====================

    @Test
    void getSchedulesByTourId_ShouldReturnSuccess() throws Exception {
        Long tourId = 1L;
        Object expectedResponse = Collections.singletonMap("data", "schedule-list");
        when(travelService.getSchedulesByTourId(tourId)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(get("/api/bff/travel/schedules/tour/{tourId}", tourId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addScheduleToTour_ShouldReturnSuccess() throws Exception {
        TourScheduleRequest input = TourScheduleRequest.builder().tourId(1L).dayNumber(1).title("Day 1").build();
        Object expectedResponse = Collections.singletonMap("data", "schedule-added");
        when(travelService.addScheduleToTour(any(TourScheduleRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post("/api/bff/travel/schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTourSchedule_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        TourScheduleRequest input = TourScheduleRequest.builder().title("Day 1 Updated").build();
        Object expectedResponse = Collections.singletonMap("data", "schedule-updated");
        when(travelService.updateTourSchedule(eq(id), any(TourScheduleRequest.class))).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(put("/api/bff/travel/schedules/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTourSchedule_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object expectedResponse = Collections.singletonMap("data", "schedule-deleted");
        when(travelService.deleteTourSchedule(id)).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(delete("/api/bff/travel/schedules/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
