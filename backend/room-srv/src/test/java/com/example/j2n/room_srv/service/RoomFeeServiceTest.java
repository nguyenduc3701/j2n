package com.example.j2n.room_srv.service;

import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.controller.request.RoomFeeDto;
import com.example.j2n.room_srv.controller.response.RoomFeeResponse;
import com.example.j2n.room_srv.repository.RoomFeeRepository;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomFeeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomFeeServiceTest {

    @Mock
    private RoomFeeRepository roomFeeRepository;

    @Mock
    private FeeService feeService;

    @InjectMocks
    private RoomFeeService roomFeeService;

    @Test
    void updateRoomFees_Success() {
        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomFeeDto feeDto = RoomFeeDto.builder().feeId(10L).build();
        FeeEntity feeEntity = new FeeEntity();
        feeEntity.setId(10L);
        feeEntity.setName("WATER");
        feeEntity.setUnitPrice(BigDecimal.valueOf(10000));
        feeEntity.setUnitName("m3");

        when(feeService.findAllByIds(List.of(10L))).thenReturn(List.of(feeEntity));

        // When saveAll is called, return entities populated with mocked IDs
        when(roomFeeRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<RoomFeeEntity> entities = invocation.getArgument(0);
            for (RoomFeeEntity entity : entities) {
                entity.setId(100L);
            }
            return entities;
        });

        List<RoomFeeResponse> responses = roomFeeService.updateRoomFees(room, List.of(feeDto));

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());
        assertEquals(10L, responses.get(0).getFeeId());
        assertEquals("WATER", responses.get(0).getName());
        assertEquals(BigDecimal.valueOf(10000), responses.get(0).getUnitPrice());
        assertEquals("m3", responses.get(0).getUnitName());

        verify(roomFeeRepository, times(1)).deleteByRoomId(1L);
        verify(feeService, times(1)).findAllByIds(List.of(10L));
        verify(roomFeeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateRoomFees_FeeNotFound_ThrowsException() {
        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomFeeDto feeDto = RoomFeeDto.builder().feeId(99L).build();

        // Return empty list simulating fee not found
        when(feeService.findAllByIds(List.of(99L))).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            roomFeeService.updateRoomFees(room, List.of(feeDto));
        });

        verify(roomFeeRepository, times(1)).deleteByRoomId(1L);
        verify(feeService, times(1)).findAllByIds(List.of(99L));
        verify(roomFeeRepository, never()).saveAll(anyList());
    }
}
