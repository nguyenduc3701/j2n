package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.repository.RoomAssetRepository;
import com.example.j2n.room_srv.repository.entity.RoomAssetEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAssetServiceTest {

    @Mock
    private RoomAssetRepository roomAssetRepository;

    @InjectMocks
    private RoomAssetService roomAssetService;

    @Test
    void saveAll_Success() {
        List<RoomAssetEntity> assets = List.of(new RoomAssetEntity());

        roomAssetService.saveAll(assets);

        verify(roomAssetRepository, times(1)).saveAll(assets);
    }

    @Test
    void getAssetsByRoomId_Success() {
        Long roomId = 1L;
        List<RoomAssetEntity> expectedAssets = List.of(new RoomAssetEntity());
        when(roomAssetRepository.findByRoomId(roomId)).thenReturn(expectedAssets);

        List<RoomAssetEntity> actualAssets = roomAssetService.getAssetsByRoomId(roomId);

        assertNotNull(actualAssets);
        assertEquals(expectedAssets, actualAssets);
        verify(roomAssetRepository, times(1)).findByRoomId(roomId);
    }
}
