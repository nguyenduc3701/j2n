package com.example.j2n.bff_srv.controller.request;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@EqualsAndHashCode(callSuper = true)
public class SearchUserRequest extends PagingRequest {
    private Optional<String> fullName;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<Long> roleId;
    private Optional<String> status;
    private Optional<Long> id;
    private Optional<String> userName;
    private Optional<LocalDate> startDate;
    private Optional<LocalDate> endDate;
}
