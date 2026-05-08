package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.RoomUtilityConfigDto;
import com.example.j2n.room_srv.controller.request.UpdateRoomUtilityRequest;
import com.example.j2n.room_srv.controller.response.RoomResponse;
import com.example.j2n.room_srv.controller.response.RoomUtilityResponse;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.RoomUtilityRepository;
import com.example.j2n.room_srv.repository.UtilityConfigRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomUtilityEntity;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.dto.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomUtilityRepository roomUtilityRepository;

    @Mock
    private UtilityConfigRepository utilityConfigRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void createRoom_Success() {
        RoomRequest request = RoomRequest.builder()
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .area("20m2")
                .totalPeople(2)
                .build();

        RoomEntity entity = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .area("20m2")
                .totalPeople(2)
                .build();

        when(roomRepository.save(any(RoomEntity.class))).thenReturn(entity);

        BaseResponse<RoomResponse> response = roomService.createRoom(request);

        assertNotNull(response);
        assertEquals("101", response.getData().getRoomNumber());
        verify(roomRepository, times(1)).save(any(RoomEntity.class));
    }

    @Test
    void getRoomById_NotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomService.getRoomById(1L));
    }

    @Test
    void updateRoom_Success() {
        Long id = 1L;
        RoomRequest request = RoomRequest.builder()
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2500000))
                .area("25m2")
                .totalPeople(3)
                .build();

        RoomEntity existingRoom = RoomEntity.builder()
                .id(id)
                .roomNumber("101")
                .basePrice(BigDecimal.valueOf(2000000))
                .area("20m2")
                .totalPeople(2)
                .build();

        when(roomRepository.findById(id)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(existingRoom);

        BaseResponse<RoomResponse> response = roomService.updateRoom(id, request);

        assertEquals(BigDecimal.valueOf(2500000), existingRoom.getBasePrice());
        assertEquals("25m2", existingRoom.getArea());
        assertEquals(3, existingRoom.getTotalPeople());
    }

    @Test
    void updateRoomUtilities_Success() {
        Long roomId = 1L;
        Long configId = 10L;
        UpdateRoomUtilityRequest request = UpdateRoomUtilityRequest.builder()
                .utilityConfigs(List.of(
                        RoomUtilityConfigDto.builder()
                                .utilityConfigId(configId)
                                .quantity(1)
                                .build()
                ))
                .build();

        RoomEntity room = RoomEntity.builder().id(roomId).build();
        UtilityConfigEntity config = UtilityConfigEntity.builder()
                .id(configId)
                .name("Electric")
                .type("ELECTRIC")
                .unitPrice(BigDecimal.valueOf(3500))
                .unitName("kWh")
                .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(utilityConfigRepository.findById(configId)).thenReturn(Optional.of(config));
        when(roomUtilityRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        BaseResponse<List<RoomUtilityResponse>> response = roomService.updateRoomUtilities(roomId, request);

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("Electric", response.getData().get(0).getName());
        verify(roomUtilityRepository, times(1)).deleteByRoomId(roomId);
        verify(roomUtilityRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateRoomUtilities_RoomNotFound() {
        Long roomId = 1L;
        UpdateRoomUtilityRequest request = UpdateRoomUtilityRequest.builder()
                .utilityConfigs(new ArrayList<>())
                .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomService.updateRoomUtilities(roomId, request));
    }

    @Test
    void updateRoomUtilities_UtilityConfigNotFound() {
        Long roomId = 1L;
        Long configId = 10L;
        UpdateRoomUtilityRequest request = UpdateRoomUtilityRequest.builder()
                .utilityConfigs(List.of(
                        RoomUtilityConfigDto.builder()
                                .utilityConfigId(configId)
                                .quantity(1)
                                .build()
                ))
                .build();

        RoomEntity room = RoomEntity.builder().id(roomId).build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(utilityConfigRepository.findById(configId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomService.updateRoomUtilities(roomId, request));
    }
}
