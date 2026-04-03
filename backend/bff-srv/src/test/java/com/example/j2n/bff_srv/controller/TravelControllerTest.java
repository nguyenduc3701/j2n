package com.example.j2n.bff_srv.controller;

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
import java.util.Map;

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
        Object input = Map.of("name", "New Category");
        Object expectedResponse = Collections.singletonMap("data", "created");
        when(travelService.createCategory(any())).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post(CATEGORY_BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCategory_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object input = Map.of("name", "Updated Category");
        Object expectedResponse = Collections.singletonMap("data", "updated");
        when(travelService.updateCategory(eq(id), any())).thenReturn(Mono.just(expectedResponse));

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
        Object input = Map.of("title", "Tour Nha Trang", "categoryId", 1);
        Object expectedResponse = Collections.singletonMap("data", "tour-created");
        when(travelService.createTour(any())).thenReturn(Mono.just(expectedResponse));

        mockMvc.perform(post(TOUR_BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTour_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        Object input = Map.of("title", "Tour Đà Nẵng Updated");
        Object expectedResponse = Collections.singletonMap("data", "tour-updated");
        when(travelService.updateTour(eq(id), any())).thenReturn(Mono.just(expectedResponse));

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
}
