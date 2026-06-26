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
import com.example.j2n.room_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.room_srv.messaging.user.event.UserUpdatedEvent;
import com.example.j2n.room_srv.messaging.user.event.UserDeletedEvent;
import com.example.j2n.room_srv.messaging.room.event.RoomMemberMappedEvent;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
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

    @Mock
    private RoomEventPublisher roomEventPublisher;

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
        verify(roomEventPublisher, times(1)).publishRoomMemberMapped(any(RoomMemberMappedEvent.class));
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
        verify(roomEventPublisher, times(1)).publishRoomMemberMapped(any(RoomMemberMappedEvent.class));
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
    void getRoomMembersByRoomId_Success() {
        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity member1 = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(true)
                .build();
        RoomMemberEntity member2 = RoomMemberEntity.builder()
                .id(101L)
                .room(room)
                .userId(11L)
                .isPrimary(false)
                .build();

        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of(member1, member2));

        BaseResponse<List<RoomMemberResponse>> result = roomMemberService.getRoomMembersByRoomId(1L);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals(100L, result.getData().get(0).getId());
        assertEquals(10L, result.getData().get(0).getUserId());
        assertEquals(101L, result.getData().get(1).getId());
        assertEquals(11L, result.getData().get(1).getUserId());

        verify(roomMemberRepository, times(1)).findByRoomId(1L);
    }

    @Test
    void mapMemberToRoom_TriggersOccupiedStatus() {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .isPrimary(false)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .maxPeople(1)
                .status("AVAILABLE")
                .build();

        RoomMemberEntity savedEntity = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.empty());
        when(roomMemberRepository.findByRoomId(1L))
                .thenReturn(List.of()) // first call (capacity check)
                .thenReturn(List.of(savedEntity)); // second call (status update check)
        when(roomMemberRepository.saveAll(anyList())).thenReturn(List.of(savedEntity));

        BaseResponse<List<RoomMemberResponse>> result = roomMemberService.mapMemberToRoom(request);

        assertNotNull(result);
        verify(roomService, times(1)).updateRoomStatus(room, "OCCUPIED");
    }

    @Test
    void handleUserRegisteredEvent_TriggersOccupiedStatus() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId("10")
                .roomId("1")
                .role("RENTER")
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .maxPeople(1)
                .status("AVAILABLE")
                .build();

        RoomMemberEntity savedEntity = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .build();

        when(roomMemberRepository.existsByRoomIdAndUserId(1L, 10L)).thenReturn(false);
        when(roomService.findRoomByIdOrThrow(1L)).thenReturn(room);
        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of(savedEntity));

        roomMemberService.handleUserRegisteredEvent(event);

        verify(roomMemberRepository, times(1)).save(any(RoomMemberEntity.class));
        verify(roomService, times(1)).updateRoomStatus(room, "OCCUPIED");
    }

    @Test
    void deleteRoomMemberByUserId_Success() {
        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity existingMember = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(existingMember));

        BaseResponse<Void> result = roomMemberService.deleteRoomMemberByUserId(10L);

        assertNotNull(result);
        assertNull(result.getData());

        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, times(1)).delete(existingMember);
        verify(roomMemberRepository, times(1)).flush();
        verify(roomEventPublisher, times(1)).publishRoomMemberRemoved(any());
    }

    @Test
    void deleteRoomMemberByUserId_NotFound() {
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> roomMemberService.deleteRoomMemberByUserId(10L));

        verify(roomMemberRepository, times(1)).findByUserId(10L);
        verify(roomMemberRepository, never()).delete(any());
    }

    @Test
    void deleteRoomMemberByUserId_TriggersAvailableStatus() {
        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .maxPeople(2)
                .status("OCCUPIED")
                .build();

        RoomMemberEntity existingMember = RoomMemberEntity.builder()
                .id(100L)
                .room(room)
                .userId(10L)
                .isPrimary(false)
                .build();

        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(existingMember));
        when(roomMemberRepository.findByRoomId(1L)).thenReturn(List.of()); // 0 members left after delete

        roomMemberService.deleteRoomMemberByUserId(10L);

        verify(roomMemberRepository, times(1)).delete(existingMember);
        verify(roomMemberRepository, times(1)).flush();
        verify(roomService, times(1)).updateRoomStatus(room, "AVAILABLE");
        verify(roomEventPublisher, times(1)).publishRoomMemberRemoved(any());
    }

    // ==================== User Event Handling Tests ====================

    @Test
    void handleUserUpdatedEvent_RoleNotRenter() {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId("10")
                .role("ADMIN")
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity member = RoomMemberEntity.builder().id(100L).room(room).userId(10L).build();

        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(member));

        roomMemberService.handleUserUpdatedEvent(event);

        verify(roomMemberRepository, times(1)).delete(member);
        verify(roomMemberRepository, times(1)).flush();
    }

    @Test
    void handleUserUpdatedEvent_RoomChanged() {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId("10")
                .role("RENTER")
                .roomId("2")
                .oldRoomId("1")
                .build();

        RoomEntity oldRoom = RoomEntity.builder().id(1L).build();
        RoomEntity newRoom = RoomEntity.builder().id(2L).build();
        RoomMemberEntity oldMember = RoomMemberEntity.builder().id(100L).room(oldRoom).userId(10L).build();

        // When processing oldRoomId: find by user and delete
        when(roomMemberRepository.findByUserId(10L))
                .thenReturn(Optional.of(oldMember)) // for oldRoomId check
                .thenReturn(Optional.empty()); // for newRoomId check (not exists in new room yet)

        when(roomMemberRepository.existsByRoomIdAndUserId(2L, 10L)).thenReturn(false);
        when(roomService.findRoomByIdOrThrow(2L)).thenReturn(newRoom);

        roomMemberService.handleUserUpdatedEvent(event);

        verify(roomMemberRepository, times(2)).findByUserId(10L);
        verify(roomMemberRepository, times(1)).delete(oldMember);
        verify(roomMemberRepository, times(1)).save(any(RoomMemberEntity.class));
    }

    @Test
    void handleUserUpdatedEvent_RoomRemoved() {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId("10")
                .role("RENTER")
                .roomId(null)
                .oldRoomId("1")
                .build();

        RoomEntity oldRoom = RoomEntity.builder().id(1L).build();
        RoomMemberEntity oldMember = RoomMemberEntity.builder().id(100L).room(oldRoom).userId(10L).build();

        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(oldMember));

        roomMemberService.handleUserUpdatedEvent(event);

        verify(roomMemberRepository, times(2)).findByUserId(10L); // once for oldRoomId block, once for else branch (delete quietly)
        verify(roomMemberRepository, times(2)).delete(oldMember);
    }

    @Test
    void handleUserDeletedEvent_Success() {
        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId("10")
                .build();

        RoomEntity room = RoomEntity.builder().id(1L).build();
        RoomMemberEntity member = RoomMemberEntity.builder().id(100L).room(room).userId(10L).build();

        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.of(member));

        roomMemberService.handleUserDeletedEvent(event);

        verify(roomMemberRepository, times(1)).delete(member);
        verify(roomMemberRepository, times(1)).flush();
    }

    @Test
    void deleteRoomMemberByUserIdQuietly_NotFound() {
        when(roomMemberRepository.findByUserId(10L)).thenReturn(Optional.empty());

        roomMemberService.deleteRoomMemberByUserIdQuietly(10L);

        verify(roomMemberRepository, never()).delete(any());
        verify(roomMemberRepository, never()).flush();
    }
}
