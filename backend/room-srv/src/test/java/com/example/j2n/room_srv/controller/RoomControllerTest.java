package com.example.j2n.room_srv.controller;

import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.service.response.RoomFeeResponse;
import com.example.j2n.room_srv.service.response.RoomResponse;
import com.example.j2n.room_srv.service.response.SearchRoomsResponse;
import com.example.j2n.room_srv.service.RoomService;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchRooms_Success() throws Exception {
        SearchRoomsRequest request = new SearchRoomsRequest();
        SearchRoomsResponse responseData = new SearchRoomsResponse();
        when(roomService.searchRooms(any(SearchRoomsRequest.class))).thenReturn(ResponseFactory.success(responseData));

        mockMvc.perform(post("/rooms/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).searchRooms(any(SearchRoomsRequest.class));
    }

    @Test
    void getRoomById_Success() throws Exception {
        Long roomId = 1L;
        RoomResponse responseData = new RoomResponse();
        when(roomService.getRoomById(roomId)).thenReturn(ResponseFactory.success(responseData));

        mockMvc.perform(get("/rooms/" + roomId))
                .andExpect(status().isOk());

        verify(roomService, times(1)).getRoomById(roomId);
    }

    @Test
    void updateRoom_Success() throws Exception {
        Long roomId = 1L;
        RoomRequest request = new RoomRequest();
        RoomResponse responseData = new RoomResponse();
        when(roomService.updateRoom(eq(roomId), any(RoomRequest.class))).thenReturn(ResponseFactory.success(responseData));

        mockMvc.perform(put("/rooms/" + roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).updateRoom(eq(roomId), any(RoomRequest.class));
    }

    @Test
    void updateRoomFees_Success() throws Exception {
        Long roomId = 1L;
        UpdateRoomFeeRequest request = UpdateRoomFeeRequest.builder()
                .feeIds(List.of(1, 2))
                .build();
        List<RoomFeeResponse> responseData = List.of(new RoomFeeResponse());
        when(roomService.updateRoomFees(eq(roomId), any(UpdateRoomFeeRequest.class))).thenReturn(ResponseFactory.success(responseData));

        mockMvc.perform(put("/rooms/" + roomId + "/fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).updateRoomFees(eq(roomId), any(UpdateRoomFeeRequest.class));
    }
}
