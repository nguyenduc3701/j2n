package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.MapMemberToRoomRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomMemberRequest;
import com.example.j2n.room_srv.service.RoomMemberService;
import com.example.j2n.room_srv.service.response.RoomMemberResponse;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Room Member Controller", description = "Endpoints for managing room members and room-member mappings")
public class RoomMemberController {

    private final RoomMemberService roomMemberService;

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get room members by room id", description = "Get all members mapped to a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<List<RoomMemberResponse>>> getRoomMembersByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomMemberService.getRoomMembersByRoomId(roomId));
    }

    @PostMapping("/mapping")
    @Operation(summary = "Map member to room", description = "Assign a user as a member to a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.MEMBER_ALREADY_MAPPED),
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_MAX_PEOPLE_EXCEEDED)
            })
    })
    public ResponseEntity<BaseResponse<List<RoomMemberResponse>>> mapMemberToRoom(
            @Valid @RequestBody MapMemberToRoomRequest request) {
        return ResponseEntity.ok(roomMemberService.mapMemberToRoom(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room member", description = "Update attributes of a room member (e.g. primary status)")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.MEMBER_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<RoomMemberResponse>> updateRoomMember(
            @PathVariable Long id, @Valid @RequestBody UpdateRoomMemberRequest request) {
        return ResponseEntity.ok(roomMemberService.updateRoomMember(id, request));
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete room member by user id", description = "Remove a user from their assigned room by user ID")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.MEMBER_NOT_FOUND_BY_USER_ID)
            })
    })
    public ResponseEntity<BaseResponse<Void>> deleteRoomMemberByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(roomMemberService.deleteRoomMemberByUserId(userId));
    }
}
