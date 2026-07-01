package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.MapMemberToRoomRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomMemberRequest;
import com.example.j2n.room_srv.service.RoomMemberService;
import com.example.j2n.room_srv.service.response.RoomMemberResponse;
import com.example.j2n.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomMemberService roomMemberService;

    @MockitoBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRoomMembersByRoomId_Success() throws Exception {
        RoomMemberResponse response = RoomMemberResponse.builder()
                .id(100L)
                .roomId(1L)
                .userId(10L)
                .isPrimary(true)
                .joinedAt(LocalDateTime.now())
                .build();

        when(roomMemberService.getRoomMembersByRoomId(1L))
                .thenReturn(ResponseFactory.success(List.of(response)));

        mockMvc.perform(get("/room/members/room/1")
                .contextPath("/room"))
                .andExpect(status().isOk());

        verify(roomMemberService, times(1)).getRoomMembersByRoomId(1L);
    }

    @Test
    void mapMemberToRoom_Success() throws Exception {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .users(List.of(10L))
                .build();

        RoomMemberResponse response = RoomMemberResponse.builder()
                .id(100L)
                .roomId(1L)
                .userId(10L)
                .isPrimary(true)
                .joinedAt(LocalDateTime.now())
                .build();

        when(roomMemberService.mapMemberToRoom(any(MapMemberToRoomRequest.class)))
                .thenReturn(ResponseFactory.success(List.of(response)));

        mockMvc.perform(post("/room/members/mapping")
                .contextPath("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomMemberService, times(1)).mapMemberToRoom(any(MapMemberToRoomRequest.class));
    }

    @Test
    void updateRoomMemberByUserId_Success() throws Exception {
        UpdateRoomMemberRequest request = UpdateRoomMemberRequest.builder()
                .isPrimary(Optional.of(true))
                .build();

        RoomMemberResponse response = RoomMemberResponse.builder()
                .id(100L)
                .roomId(1L)
                .userId(10L)
                .isPrimary(true)
                .joinedAt(LocalDateTime.now())
                .build();

        when(roomMemberService.updateRoomMemberByUserId(eq(10L), any(UpdateRoomMemberRequest.class)))
                .thenReturn(ResponseFactory.success(response));

        mockMvc.perform(put("/room/members/10")
                .contextPath("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomMemberService, times(1)).updateRoomMemberByUserId(eq(10L), any(UpdateRoomMemberRequest.class));
    }

    @Test
    void deleteRoomMemberByUserId_Success() throws Exception {
        when(roomMemberService.deleteRoomMemberByUserId(10L)).thenReturn(ResponseFactory.success(null));

        mockMvc.perform(delete("/room/members/user/10")
                .contextPath("/room"))
                .andExpect(status().isOk());

        verify(roomMemberService, times(1)).deleteRoomMemberByUserId(10L);
    }
}
