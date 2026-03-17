package com.example.j2n.auth_srv.controllers.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.j2n.dto.PagingRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchUsersRequest extends PagingRequest {
    @Schema(description = "Filter by full name", example = "John")
    private Optional<String> fullName = Optional.empty();

    @Schema(description = "Filter by email", example = "user@example.com")
    private Optional<String> email = Optional.empty();

    @Schema(description = "Filter by phone number", example = "0987")
    private Optional<String> phoneNumber = Optional.empty();

    @Schema(description = "Filter by role ID", example = "1")
    private Optional<Long> roleId = Optional.empty();

    @Schema(description = "Filter by status", example = "ACTIVE")
    private Optional<String> status = Optional.empty();

    @Schema(description = "Filter by user ID", example = "1")
    private Optional<Long> id = Optional.empty();

    @Schema(description = "Filter by username", example = "admin")
    private Optional<String> userName = Optional.empty();

    @Schema(description = "Filter by start date", example = "2023-01-01")
    private Optional<LocalDate> startDate = Optional.empty();

    @Schema(description = "Filter by end date", example = "2023-12-31")
    private Optional<LocalDate> endDate = Optional.empty();

    @Schema(description = "Filter by image URL", example = "http://example.com/avatar.png")
    private Optional<String> imageUrl = Optional.empty();
}
