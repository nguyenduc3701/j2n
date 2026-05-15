package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.service.response.SearchAssetsResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.*;
import com.example.j2n.room_srv.service.response.AssetResponse;
import com.example.j2n.room_srv.service.AssetService;
import com.example.j2n.swagger.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Controller", description = "Endpoints for managing room assets")
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/search")
    @Operation(summary = "Search assets", description = "Retrieve a paginated list of assets matching criteria")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<SearchAssetsResponse>> searchAssets(@Valid @RequestBody SearchAssetsRequest request) {
        return ResponseEntity.ok(assetService.searchAssets(request));
    }

    @PostMapping
    @Operation(summary = "Create asset", description = "Create a new asset type")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<AssetResponse>> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update asset", description = "Update asset details")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ASSET_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<AssetResponse>> updateAsset(
            @PathVariable Long id, @Valid @RequestBody UpdateAssetRequest request) {
        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete asset", description = "Delete an asset type")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ASSET_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<Void>> deleteAsset(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.deleteAsset(id));
    }

    @PostMapping("/map-room")
    @Operation(summary = "Map asset to room", description = "Assign an asset to a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.ASSET_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<String>> mapAssetWithRoom(@Valid @RequestBody MapAssetToRoomRequest request) {
        return ResponseEntity.ok(assetService.mapAssetWithRoom(request));
    }
}
