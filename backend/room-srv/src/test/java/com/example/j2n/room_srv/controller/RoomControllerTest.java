package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.controller.response.RoomFeeResponse;
import com.example.j2n.room_srv.controller.response.RoomResponse;
import com.example.j2n.room_srv.controller.response.SearchRoomsResponse;
import com.example.j2n.room_srv.service.RoomService;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @Test
    void searchRooms_Success() {
        SearchRoomsRequest request = new SearchRoomsRequest();
        SearchRoomsResponse responseData = new SearchRoomsResponse();
        when(roomService.searchRooms(request)).thenReturn(ResponseFactory.success(responseData));

        ResponseEntity<BaseResponse<SearchRoomsResponse>> result = roomController.searchRooms(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(responseData, result.getBody().getData());
        verify(roomService, times(1)).searchRooms(request);
    }

    @Test
    void getRoomById_Success() {
        Long roomId = 1L;
        RoomResponse responseData = new RoomResponse();
        when(roomService.getRoomById(roomId)).thenReturn(ResponseFactory.success(responseData));

        ResponseEntity<BaseResponse<RoomResponse>> result = roomController.getRoomById(roomId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(responseData, result.getBody().getData());
        verify(roomService, times(1)).getRoomById(roomId);
    }

    @Test
    void updateRoom_Success() {
        Long roomId = 1L;
        RoomRequest request = new RoomRequest();
        RoomResponse responseData = new RoomResponse();
        when(roomService.updateRoom(roomId, request)).thenReturn(ResponseFactory.success(responseData));

        ResponseEntity<BaseResponse<RoomResponse>> result = roomController.updateRoom(roomId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(responseData, result.getBody().getData());
        verify(roomService, times(1)).updateRoom(roomId, request);
    }

    @Test
    void updateRoomFees_Success() {
        Long roomId = 1L;
        UpdateRoomFeeRequest request = new UpdateRoomFeeRequest();
        List<RoomFeeResponse> responseData = List.of(new RoomFeeResponse());
        when(roomService.updateRoomFees(roomId, request)).thenReturn(ResponseFactory.success(responseData));

        ResponseEntity<BaseResponse<List<RoomFeeResponse>>> result = roomController.updateRoomFees(roomId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(responseData, result.getBody().getData());
        verify(roomService, times(1)).updateRoomFees(roomId, request);
    }
}
