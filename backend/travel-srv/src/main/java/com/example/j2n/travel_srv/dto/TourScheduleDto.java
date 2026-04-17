package com.example.j2n.travel_srv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourScheduleDto {
    private Long tourId;
    private Integer dayNumber;
    private String title;
    private String content;
}
