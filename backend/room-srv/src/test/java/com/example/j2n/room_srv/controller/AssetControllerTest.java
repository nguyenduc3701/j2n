package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.*;
import com.example.j2n.room_srv.service.response.AssetResponse;
import com.example.j2n.room_srv.service.response.SearchAssetsResponse;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.service.AssetService;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssetController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssetService assetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchAssets_Success() throws Exception {
        SearchAssetsRequest request = new SearchAssetsRequest();
        when(assetService.searchAssets(any(SearchAssetsRequest.class))).thenReturn(ResponseFactory.success(new SearchAssetsResponse()));

        mockMvc.perform(post("/room/assets/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(assetService, times(1)).searchAssets(any(SearchAssetsRequest.class));
    }

    @Test
    void createAsset_Success() throws Exception {
        CreateAssetRequest request = new CreateAssetRequest();
        when(assetService.createAsset(any(CreateAssetRequest.class))).thenReturn(ResponseFactory.success(new AssetResponse()));

        mockMvc.perform(post("/room/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(assetService, times(1)).createAsset(any(CreateAssetRequest.class));
    }

    @Test
    void updateAsset_Success() throws Exception {
        UpdateAssetRequest request = new UpdateAssetRequest();
        when(assetService.updateAsset(eq(1L), any(UpdateAssetRequest.class))).thenReturn(ResponseFactory.success(new AssetResponse()));

        // We use PUT here as per the updated REST rules
        mockMvc.perform(put("/room/assets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(assetService, times(1)).updateAsset(eq(1L), any(UpdateAssetRequest.class));
    }

    @Test
    void deleteAsset_Success() throws Exception {
        when(assetService.deleteAsset(1L)).thenReturn(ResponseFactory.success(null));

        mockMvc.perform(delete("/room/assets/1"))
                .andExpect(status().isOk());

        verify(assetService, times(1)).deleteAsset(1L);
    }

    @Test
    void mapAssetWithRoom_Success() throws Exception {
        MapAssetToRoomRequest request = new MapAssetToRoomRequest();
        when(assetService.mapAssetWithRoom(any(MapAssetToRoomRequest.class)))
                .thenReturn(ResponseFactory.success(MessageEnum.MAP_ASSET_TO_ROOM_SUCCESS.getMessage()));

        mockMvc.perform(post("/room/assets/map-room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(assetService, times(1)).mapAssetWithRoom(any(MapAssetToRoomRequest.class));
    }
}
