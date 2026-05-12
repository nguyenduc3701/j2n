package com.example.j2n.room_srv.controller.response;

import com.example.j2n.dto.PagingResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchAssetsResponse {

    @Schema(description = "List of assets matching the search criteria", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AssetResponse> assets;

    @Schema(description = "Paging information", requiredMode = Schema.RequiredMode.REQUIRED)
    private PagingResponse page;
}
