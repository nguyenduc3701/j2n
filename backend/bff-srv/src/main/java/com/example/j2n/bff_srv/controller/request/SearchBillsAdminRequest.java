package com.example.j2n.bff_srv.controller.request;

import com.example.j2n.dto.PagingRequest;
import com.example.j2n.bff_srv.constant.BillStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchBillsAdminRequest extends PagingRequest {

    @Schema(description = "Room ID to filter bills", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<Long> roomId = Optional.empty();

    @Schema(description = "Filter by renter user ID", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<Long> renterId = Optional.empty();

    @Schema(description = "Filter by billing month", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<Integer> billingMonth = Optional.empty();

    @Schema(description = "Filter by bill status", example = "UNPAID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Optional<BillStatus> status = Optional.empty();
}
