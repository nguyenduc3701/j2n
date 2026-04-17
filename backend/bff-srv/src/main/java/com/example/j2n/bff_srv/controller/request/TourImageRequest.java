package com.example.j2n.bff_srv.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourImageRequest {
    @NotNull(message = "Tour ID is required")
    private Long tourId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private Boolean isPrimary;
}
