package com.example.j2n.room_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.controller.request.MapMemberToRoomRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomMemberRequest;
import com.example.j2n.room_srv.repository.RoomMemberRepository;
import com.example.j2n.room_srv.service.RoomService;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.service.response.RoomMemberResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomMemberServiceTest {

    @Mock
    private RoomMemberRepository roomMemberRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomMemberService roomMemberService;

    @Test
    void mapMemberToRoom_Success_NotPrimary() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .isPrimary(false)
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity savedEntity = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.empty());
        when(roomMemberRepository.saveAll(anyList())).thenReturn(List.of(savedEntity));

        BaseResponse<List<RoomMemberResponse>> result = roomMemberService.mapMemberToRoom(request);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(100L, result.getData().get(0).getId());
        assertEquals(1L, result.getData().get(0).getRoomId());
        assertEquals(10L, result.getData().get(0).getUserId());
        assertFalse(result.getData().get(0).getIsPrimary());

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, never()).findByRoomId(anyLong());
        verify(roomMemberRepository, times(1)).saveAll(anyList());
    }

    @Test
    void mapMemberToRoom_Success_Primary() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .isPrimary(true)
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existingPrimary = RoomMemberEntity.builder()
                .id(99L)
                .room(room)
                .userId(5L)
                .isPrimary(true)
                .build();
        RoomMemberEntity savedEntity = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(true)
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.empty());
        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of(existingPrimary));
        when(roomMemberRepository.saveAll(anyList())).thenAnswer(i -> {
            List<RoomMemberEntity> entities = i.getArgument(0);
            return entities.stream().map(entity -> {
                if (entity.getUserId().equals(10L)) {
                    return savedEntity;
                }
                return entity;
            }).collect(java.util.stream.Collectors.toList());
        });

        BaseResponse<List<RoomMemberResponse>> result = roomMemberService.mapMemberToRoom(request);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(100L, result.getData().get(0).getId());
        assertTrue(result.getData().get(0).getIsPrimary());
        assertFalse(existingPrimary.getIsPrimary()); // verifying existing primary demoted

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, times(1)).findByRoomId(1L);
        verify(roomMemberRepository, times(1)).saveAll(anyList());
    }

    @Test
    void mapMemberToRoom_RoomNotFound() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenThrow(new DataNotFoundException(com.example.j2n.room_srv.constant.MessageEnum.ROOM_NOT_FOUND.withArgs(1L)));

        assertThrows(DataNotFoundException.class, () -> roomMemberService.mapMemberToRoom(request));

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, never()).findByUserId(anyLong());
        verify(roomMemberRepository, never()).saveAll(any());
    }

    @Test
    void mapMemberToRoom_AlreadyMember() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existing = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(existing));

        assertThrows(InvalidInputException.class, () -> roomMemberService.mapMemberToRoom(request));

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, never()).saveAll(any());
    }

    @Test
    void mapMemberToRoom_AlreadyMemberOfAnotherRoom() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomEntity anotherRoom = RoomEntity.builder().id(2L).build();
        RoomMemberEntity existing = RoomMemberEntity.builder()
                .id(100L)
                .room(anotherRoom)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(existing));

        assertThrows(InvalidInputException.class, () -> roomMemberService.mapMemberToRoom(request));

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, never()).saveAll(any());
    }

    @Test
    void mapMemberToRoom_CapacityExceeded() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L, 11L))
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).maxPeople(1).build();
        RoomMemberEntity existing = RoomMemberEntity.builder().id(99L).build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of(existing));

        assertThrows(InvalidInputException.class, () -> roomMemberService.mapMemberToRoom(request));

        verify(roomService, times(1)).findRoomByIdOrThrow(1L);
        verify(roomMemberRepository, times(1)).findByRoomId(1L);
        verify(roomMemberRepository, never()).saveAll(any());
    }

    @Test
    void updateRoomMember_Success_SetPrimary() {
        UpdateRoomMemberRequest request = UpdateRoomMemberRequest.builder()
                .isPrimary(Optional.of(true))
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existingMember = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();
        RoomMemberEntity existingPrimary = RoomMemberEntity.builder()
                .id(99L)
                .room(room)
                .userId(5L)
                .isPrimary(true)
                .build();

        when(roomMemberRepository.findById(100L)).thenReturn(Optional.of(existingMember));
        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of(existingMember, existingPrimary));
        when(roomMemberRepository.save(any(RoomMemberEntity.class))).thenAnswer(i -> i.getArgument(0));

        BaseResponse<RoomMemberResponse> result = roomMemberService.updateRoomMember(100L, request);

        assertNotNull(result);
        assertTrue(result.getData().getIsPrimary());
        assertFalse(existingPrimary.getIsPrimary());

        verify(roomMemberRepository, times(1)).findById(100L);
        verify(roomMemberRepository, times(1)).findByRoomId(1L);
        verify(roomMemberRepository, times(2)).save(any(RoomMemberEntity.class)); // demoted + saved
    }

    @Test
    void updateRoomMember_Success_NoChange() {
        UpdateRoomMemberRequest request = UpdateRoomMemberRequest.builder().build(); // empty optional

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existingMember = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(true)
                .build();

        when(roomMemberRepository.findById(100L)).thenReturn(Optional.of(existingMember));
        when(roomMemberRepository.save(any(RoomMemberEntity.class))).thenAnswer(i -> i.getArgument(0));

        BaseResponse<RoomMemberResponse> result = roomMemberService.updateRoomMember(100L, request);

        assertNotNull(result);
        assertTrue(result.getData().getIsPrimary());

        verify(roomMemberRepository, times(1)).findById(100L);
        verify(roomMemberRepository, never()).findByRoomId(anyLong());
        verify(roomMemberRepository, times(1)).save(any(RoomMemberEntity.class));
    }

    @Test
    void updateRoomMember_NotFound() {
        UpdateRoomMemberRequest request = UpdateRoomMemberRequest.builder()
                .isPrimary(Optional.of(true))
                .build();

        when(roomMemberRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomMemberService.updateRoomMember(100L, request));

        verify(roomMemberRepository, times(1)).findById(100L);
        verify(roomMemberRepository, never()).save(any());
    }

    @Test
    void deleteRoomMember_Success() {
        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existingMember = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(true)
                .build();

        when(roomMemberRepository.findById(100L)).thenReturn(Optional.of(existingMember));

        BaseResponse<Void> result = roomMemberService.deleteRoomMember(100L);

        assertNotNull(result);
        assertNull(result.getData());

        verify(roomMemberRepository, times(1)).findById(100L);
        verify(roomMemberRepository, times(1)).delete(existingMember);
    }

    @Test
    void deleteRoomMember_NotFound() {
        when(roomMemberRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomMemberService.deleteRoomMember(100L));

        verify(roomMemberRepository, times(1)).findById(100L);
        verify(roomMemberRepository, never()).delete(any());
    }
}
