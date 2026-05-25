package com.example.j2n.room_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.*;
import com.example.j2n.room_srv.service.response.AssetResponse;
import com.example.j2n.room_srv.service.response.SearchAssetsResponse;
import com.example.j2n.room_srv.repository.AssetRepository;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.entity.AssetEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.utils.SearchFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomAssetService roomAssetService;
    @Mock
    private SearchFactory searchFactory;

    @InjectMocks
    private AssetService assetService;

    @Test
    void searchAssets_Success() {
        SearchAssetsRequest request = new SearchAssetsRequest();
        AssetResponse response = AssetResponse.builder().id(1L).name("Test").build();
        Page<AssetResponse> page = new PageImpl<>(List.of(response));

        doReturn(page).when(searchFactory).searchAndMap(any(), anyList(), any(), any());

        BaseResponse<SearchAssetsResponse> result = assetService.searchAssets(request);

        assertNotNull(result);
        assertEquals(1, result.getData().getAssets().size());
        verify(searchFactory, times(1)).searchAndMap(any(), anyList(), any(), any());
    }

    @Test
    void createAsset_Success() {
        CreateAssetRequest request = CreateAssetRequest.builder().name("New Asset").description("Desc").build();
        AssetEntity entity = AssetEntity.builder().id(1L).name("New Asset").description("Desc").build();

        when(assetRepository.findByName("New Asset")).thenReturn(Optional.empty());
        when(assetRepository.save(any(AssetEntity.class))).thenReturn(entity);

        BaseResponse<AssetResponse> result = assetService.createAsset(request);

        assertNotNull(result);
        assertEquals("New Asset", result.getData().getName());
        verify(assetRepository, times(1)).save(any());
    }

    @Test
    void createAsset_AlreadyExists() {
        CreateAssetRequest request = CreateAssetRequest.builder().name("Existing Asset").description("Desc").build();
        AssetEntity existingEntity = AssetEntity.builder().id(1L).name("Existing Asset").build();

        when(assetRepository.findByName("Existing Asset")).thenReturn(Optional.of(existingEntity));

        assertThrows(InvalidInputException.class, () -> assetService.createAsset(request));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void updateAsset_Success() {
        UpdateAssetRequest request = UpdateAssetRequest.builder()
                .name(Optional.of("Updated"))
                .build();
        AssetEntity entity = AssetEntity.builder().id(1L).name("Old").isDeleted(false).build();

        when(assetRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(assetRepository.save(any(AssetEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<AssetResponse> result = assetService.updateAsset(1L, request);

        assertEquals("Updated", result.getData().getName());
    }

    @Test
    void updateAsset_NotFound() {
        UpdateAssetRequest request = new UpdateAssetRequest();
        when(assetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> assetService.updateAsset(1L, request));
    }

    @Test
    void findByIdOrThrow_AssetDeleted() {
        AssetEntity entity = AssetEntity.builder().id(1L).isDeleted(true).build();
        when(assetRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(DataNotFoundException.class, () -> assetService.updateAsset(1L, new UpdateAssetRequest()));
    }

    @Test
    void deleteAsset_Success() {
        AssetEntity entity = AssetEntity.builder().id(1L).isDeleted(false).build();
        when(assetRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(assetRepository.save(any(AssetEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        BaseResponse<Void> result = assetService.deleteAsset(1L);

        assertNotNull(result);
        assertTrue(entity.getIsDeleted());
        verify(assetRepository, times(1)).save(entity);
    }
}
