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
    private Optional<String> fullName;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<Long> roleId;
    private Optional<String> status;
    private Optional<Long> id;
    private Optional<String> userName;
    private Optional<LocalDate> startDate;
    private Optional<LocalDate> endDate;
    private Optional<String> imageUrl;

}
