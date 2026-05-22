package com.example.j2n.product_srv.service.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Data transfer object for product information")
public class ProductResponse {

    @Schema(description = "ID of the product", example = "1")
    private Long id;

    @Schema(description = "Title of the product", example = "Product Nha Trang 3N2Đ")
    private String title;

    @Schema(description = "Description of the product", example = "Khám phá Nha Trang tuyệt vời")
    private String description;

    @Schema(description = "Price of the product", example = "2500000.00")
    private BigDecimal price;

    @Schema(description = "Stock of the product", example = "10")
    private Integer stock;

    @Schema(description = "Locked stock of the product", example = "2")
    private Integer lockedStock;

    @Schema(description = "Thumbnail image URL of the product", example = "thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "Duration of the product/tour", example = "3 ngày 2 đêm")
    private String duration;

    @Schema(description = "Start location of the product/tour", example = "Hà Nội")
    private String startLocation;

    @Schema(description = "Size of the product", example = "L")
    private String size;

    @Schema(description = "Design of the product", example = "Modern")
    private String design;

    @Schema(description = "Type of the product", example = "TOUR")
    private String type;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Deleted status of the product", example = "false")
    private Boolean isDeleted;

    @Schema(description = "Category of the product")
    private CategoryInfo category;

    @Schema(description = "Schedules of the product")
    private List<ScheduleInfo> schedules;

    @Schema(description = "Images of the product")
    private List<ImageInfo> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String slug;
        private String type;
        private Boolean isDeleted;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ScheduleInfo {
        private Long id;
        private Integer dayNumber;
        private String title;
        private String content;
        private String hotel;
        private String breakfast;
        private String lunch;
        private String dinner;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ImageInfo {
        private Long id;
        private String imageUrl;
        private Boolean isPrimary;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
