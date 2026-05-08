package com.example.j2n.room_srv.controller;

import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.response.RoomResponse;
import com.example.j2n.room_srv.service.RoomService;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRooms_Success() throws Exception {
        RoomResponse room = RoomResponse.builder().id(1L).roomNumber("101").build();
        when(roomService.getAllRooms()).thenReturn(ResponseFactory.success(List.of(room)));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].room_number").value("101"));
    }

    @Test
    void createRoom_Success() throws Exception {
        RoomRequest request = RoomRequest.builder()
                .roomNumber("102")
                .basePrice(BigDecimal.valueOf(3000000))
                .area("30m2")
                .build();
        RoomResponse response = RoomResponse.builder().id(2L).roomNumber("102").build();
        
        when(roomService.createRoom(any())).thenReturn(ResponseFactory.success(response));

        mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.room_number").value("102"));
    }
}
