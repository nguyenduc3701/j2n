package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.RoomFeeDto;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.service.response.RoomResponse;
import com.example.j2n.room_srv.service.response.RoomFeeResponse;
import com.example.j2n.room_srv.service.response.SearchRoomsResponse;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.SearchFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        private RoomFeeService roomFeeService;

        @Mock
        private SearchFactory searchFactory;

        @InjectMocks
        private RoomService roomService;

        @Test
        void searchRooms_Success() {
                SearchRoomsRequest request = new SearchRoomsRequest();
                RoomEntity entity = RoomEntity.builder()
                                .id(1L)
                                .roomNumber("101")
                                .build();

                Page<RoomResponse> page = new PageImpl<>(List.of(
                                RoomResponse.builder().id(1L).roomNumber("101").build()), PageRequest.of(0, 10), 1);

                doReturn(page).when(searchFactory).searchAndMap(any(), anyList(), any(), any());

                BaseResponse<SearchRoomsResponse> response = roomService.searchRooms(request);

                assertNotNull(response);
                assertEquals(1, response.getData().getRooms().size());
                assertEquals("101", response.getData().getRooms().get(0).getRoomNumber());
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
                                .roomNumber(Optional.of("101"))
                                .basePrice(Optional.of(BigDecimal.valueOf(2500000)))
                                .area(Optional.of("25m2"))
                                .maxPeople(Optional.of(3))
                                .currentElectricIndex(Optional.of(100))
                                .build();

                RoomEntity existingRoom = RoomEntity.builder()
                                .id(id)
                                .roomNumber("101")
                                .basePrice(BigDecimal.valueOf(2000000))
                                .area("20m2")
                                .maxPeople(2)
                                .currentElectricIndex(50)
                                .build();

                when(roomRepository.findById(id)).thenReturn(Optional.of(existingRoom));
                when(roomRepository.save(any(RoomEntity.class))).thenReturn(existingRoom);

                BaseResponse<RoomResponse> response = roomService.updateRoom(id, request);

                assertEquals(BigDecimal.valueOf(2500000), existingRoom.getBasePrice());
                assertEquals("25m2", existingRoom.getArea());
                assertEquals(3, existingRoom.getMaxPeople());
                assertEquals(100, existingRoom.getCurrentElectricIndex());
        }

        @Test
        void updateRoomFees_Success() {
                Long roomId = 1L;
                Long feeId = 10L;
                UpdateRoomFeeRequest request = UpdateRoomFeeRequest.builder()
                                .fees(List.of(
                                                RoomFeeDto.builder()
                                                                .feeId(feeId)
                                                                .build()))
                                .build();

                RoomEntity room = RoomEntity.builder().id(roomId).build();
                RoomFeeResponse feeResp = RoomFeeResponse.builder()
                                .feeId(feeId)
                                .name("ELECTRIC")
                                .unitPrice(BigDecimal.valueOf(3500))
                                .unitName("kWh")
                                .build();

                when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
                when(roomFeeService.updateRoomFees(eq(room), anyList())).thenReturn(List.of(feeResp));

                BaseResponse<List<RoomFeeResponse>> response = roomService.updateRoomFees(roomId, request);

                assertNotNull(response);
                assertEquals(1, response.getData().size());
                assertEquals("ELECTRIC", response.getData().get(0).getName());
                verify(roomFeeService, times(1)).updateRoomFees(eq(room), anyList());
        }

        @Test
        void updateRoomFees_RoomNotFound() {
                Long roomId = 1L;
                UpdateRoomFeeRequest request = UpdateRoomFeeRequest.builder()
                                .fees(new ArrayList<>())
                                .build();

                when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

                assertThrows(DataNotFoundException.class, () -> roomService.updateRoomFees(roomId, request));
        }
}
