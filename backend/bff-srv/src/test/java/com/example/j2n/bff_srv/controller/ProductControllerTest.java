package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.CategoryRequest;
import com.example.j2n.bff_srv.controller.request.ProductImageRequest;
import com.example.j2n.bff_srv.controller.request.ProductRequest;
import com.example.j2n.bff_srv.controller.request.ProductScheduleRequest;
import com.example.j2n.bff_srv.service.ProductService;
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
@WebMvcTest(ProductController.class)
class TravelControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductService productService;

        @Autowired
        private ObjectMapper objectMapper;

        private static final String CATEGORY_BASE_URL = "/api/bff/product/categories";
        private static final String PRODUCT_BASE_URL = "/api/bff/product";

        // ===================== Category Endpoints =====================

        @Test
        void getAllCategories_ShouldReturnSuccess() throws Exception {
                Object expectedResponse = Collections.singletonMap("data", "list");
                when(productService.getAllCategories()).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(CATEGORY_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getCategoryById_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "item");
                when(productService.getCategoryById(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(CATEGORY_BASE_URL + "/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getCategoryBySlug_ShouldReturnSuccess() throws Exception {
                String slug = "test-slug";
                Object expectedResponse = Collections.singletonMap("data", "item-by-slug");
                when(productService.getCategoryBySlug(slug)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(CATEGORY_BASE_URL + "/slug/{slug}", slug)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getCategoryByName_ShouldReturnSuccess() throws Exception {
                String name = "test-name";
                Object expectedResponse = Collections.singletonMap("data", "item-by-name");
                when(productService.getCategoryByName(name)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(CATEGORY_BASE_URL + "/name/{name}", name)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void createCategory_ShouldReturnSuccess() throws Exception {
                CategoryRequest input = CategoryRequest.builder().name("New Category").slug("new-category").build();
                Object expectedResponse = Collections.singletonMap("data", "created");
                when(productService.createCategory(any(CategoryRequest.class))).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(post(CATEGORY_BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void updateCategory_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                CategoryRequest input = CategoryRequest.builder().name("Updated Category").slug("updated-category")
                                .build();
                Object expectedResponse = Collections.singletonMap("data", "updated");
                when(productService.updateCategory(eq(id), any(CategoryRequest.class)))
                                .thenReturn(Mono.just(expectedResponse));

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
                when(productService.deleteCategory(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(delete(CATEGORY_BASE_URL + "/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        // ===================== Product Endpoints =====================

        @Test
        void getAllProducts_ShouldReturnSuccess() throws Exception {
                Object expectedResponse = Collections.singletonMap("data", "product-list");
                when(productService.getAllProducts()).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(PRODUCT_BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getProductById_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "product-item");
                when(productService.getProductById(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(PRODUCT_BASE_URL + "/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void getProductsByCategory_ShouldReturnSuccess() throws Exception {
                Long categoryId = 2L;
                Object expectedResponse = Collections.singletonMap("data", "products-by-category");
                when(productService.getProductsByCategory(categoryId)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get(PRODUCT_BASE_URL + "/category/{categoryId}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void createProduct_ShouldReturnSuccess() throws Exception {
                ProductRequest input = ProductRequest.builder().title("Product Nha Trang").categoryId(1L)
                                .price(java.math.BigDecimal.valueOf(100)).build();
                Object expectedResponse = Collections.singletonMap("data", "product-created");
                when(productService.createProduct(any(ProductRequest.class))).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(post(PRODUCT_BASE_URL)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void updateProduct_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                ProductRequest input = ProductRequest.builder().title("Product Đà Nẵng Updated").build();
                Object expectedResponse = Collections.singletonMap("data", "product-updated");
                when(productService.updateProduct(eq(id), any(ProductRequest.class)))
                                .thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(put(PRODUCT_BASE_URL + "/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void deleteProduct_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "product-deleted");
                when(productService.deleteProduct(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(delete(PRODUCT_BASE_URL + "/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        // ===================== Product Image Endpoints =====================

        @Test
        void getImagesByProductId_ShouldReturnSuccess() throws Exception {
                Long productId = 1L;
                Object expectedResponse = Collections.singletonMap("data", "image-list");
                when(productService.getImagesByProductId(productId)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get("/api/bff/product/images/product/{productId}", productId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void addImageToProduct_ShouldReturnSuccess() throws Exception {
                ProductImageRequest input = ProductImageRequest.builder().productId(1L).imageUrl("http://image.com")
                                .build();
                Object expectedResponse = Collections.singletonMap("data", "image-added");
                when(productService.addImageToProduct(any(ProductImageRequest.class)))
                                .thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(post("/api/bff/product/images")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void deleteProductImage_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "image-deleted");
                when(productService.deleteProductImage(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(delete("/api/bff/product/images/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void setPrimaryImage_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "image-primary-set");
                when(productService.setPrimaryImage(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(patch("/api/bff/product/images/{id}/primary", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        // ===================== Product Schedule Endpoints =====================

        @Test
        void getSchedulesByProductId_ShouldReturnSuccess() throws Exception {
                Long productId = 1L;
                Object expectedResponse = Collections.singletonMap("data", "schedule-list");
                when(productService.getSchedulesByProductId(productId)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(get("/api/bff/product/schedules/product/{productId}", productId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        void addScheduleToProduct_ShouldReturnSuccess() throws Exception {
                ProductScheduleRequest input = ProductScheduleRequest.builder().productId(1L).dayNumber(1)
                                .title("Day 1")
                                .build();
                Object expectedResponse = Collections.singletonMap("data", "schedule-added");
                when(productService.addScheduleToProduct(any(ProductScheduleRequest.class)))
                                .thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(post("/api/bff/product/schedules")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void updateProductSchedule_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                ProductScheduleRequest input = ProductScheduleRequest.builder().title("Day 1 Updated").build();
                Object expectedResponse = Collections.singletonMap("data", "schedule-updated");
                when(productService.updateProductSchedule(eq(id), any(ProductScheduleRequest.class)))
                                .thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(put("/api/bff/product/schedules/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input)))
                                .andExpect(status().isOk());
        }

        @Test
        void deleteProductSchedule_ShouldReturnSuccess() throws Exception {
                Long id = 1L;
                Object expectedResponse = Collections.singletonMap("data", "schedule-deleted");
                when(productService.deleteProductSchedule(id)).thenReturn(Mono.just(expectedResponse));

                mockMvc.perform(delete("/api/bff/product/schedules/{id}", id)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }
}
