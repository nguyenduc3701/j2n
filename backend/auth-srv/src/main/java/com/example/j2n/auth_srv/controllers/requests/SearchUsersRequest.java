package com.example.j2n.auth_srv.controllers.requests;

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
    private Optional<String> fullName = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Optional<String> phoneNumber = Optional.empty();
    private Optional<Long> roleId = Optional.empty();
    private Optional<String> status = Optional.empty();
    private Optional<Long> id = Optional.empty();
    private Optional<String> userName = Optional.empty();
    private Optional<LocalDate> startDate = Optional.empty();
    private Optional<LocalDate> endDate = Optional.empty();
    private Optional<String> imageUrl = Optional.empty();

}
