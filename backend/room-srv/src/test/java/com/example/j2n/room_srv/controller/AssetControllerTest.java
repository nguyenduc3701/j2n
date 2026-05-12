package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.*;
import com.example.j2n.room_srv.controller.response.AssetResponse;
import com.example.j2n.room_srv.controller.response.SearchAssetsResponse;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.service.AssetService;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    @Test
    void searchAssets_Success() {
        SearchAssetsRequest request = new SearchAssetsRequest();
        when(assetService.searchAssets(request)).thenReturn(ResponseFactory.success(new SearchAssetsResponse()));

        ResponseEntity<BaseResponse<SearchAssetsResponse>> result = assetController.searchAssets(request);

        assertEquals(200, result.getStatusCode().value());
        verify(assetService, times(1)).searchAssets(request);
    }

    @Test
    void createAsset_Success() {
        CreateAssetRequest request = new CreateAssetRequest();
        when(assetService.createAsset(request)).thenReturn(ResponseFactory.success(new AssetResponse()));

        ResponseEntity<BaseResponse<AssetResponse>> result = assetController.createAsset(request);

        assertEquals(200, result.getStatusCode().value());
        verify(assetService, times(1)).createAsset(request);
    }

    @Test
    void updateAsset_Success() {
        UpdateAssetRequest request = new UpdateAssetRequest();
        when(assetService.updateAsset(1L, request)).thenReturn(ResponseFactory.success(new AssetResponse()));

        ResponseEntity<BaseResponse<AssetResponse>> result = assetController.updateAsset(1L, request);

        assertEquals(200, result.getStatusCode().value());
        verify(assetService, times(1)).updateAsset(1L, request);
    }

    @Test
    void deleteAsset_Success() {
        when(assetService.deleteAsset(1L)).thenReturn(ResponseFactory.success(null));

        ResponseEntity<BaseResponse<Void>> result = assetController.deleteAsset(1L);

        assertEquals(200, result.getStatusCode().value());
        verify(assetService, times(1)).deleteAsset(1L);
    }

    @Test
    void mapAssetWithRoom_Success() {
        MapAssetToRoomRequest request = new MapAssetToRoomRequest();
        when(assetService.mapAssetWithRoom(request)).thenReturn(ResponseFactory.success(MessageEnum.MAP_ASSET_TO_ROOM_SUCCESS.getMessage()));

        ResponseEntity<BaseResponse<String>> result = assetController.mapAssetWithRoom(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(MessageEnum.MAP_ASSET_TO_ROOM_SUCCESS.getMessage(), result.getBody().getData());
        verify(assetService, times(1)).mapAssetWithRoom(request);
    }
}
