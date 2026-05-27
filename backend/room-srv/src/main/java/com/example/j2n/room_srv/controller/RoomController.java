package com.example.j2n.room_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.room_srv.controller.request.CreateRoomRequest;
import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomAssetRequest;
import com.example.j2n.room_srv.service.response.RoomResponse;
import com.example.j2n.room_srv.service.response.RoomFeeResponse;
import com.example.j2n.room_srv.service.response.SearchRoomsResponse;
import com.example.j2n.room_srv.service.RoomService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.swagger.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Room Controller", description = "Endpoints for managing rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/search")
    @Operation(summary = "Search rooms", description = "Retrieve a list of rooms matching the search criteria")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<SearchRoomsResponse>> searchRooms(@Valid @RequestBody SearchRoomsRequest request) {
        return ResponseEntity.ok(roomService.searchRooms(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieve detailed information about a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room information", description = "Update the details of an existing room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<RoomResponse>> updateRoom(
            @PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PutMapping("/{id}/fees")
    @Operation(summary = "Update room fees", description = "Update the list of fee configurations for a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<List<RoomFeeResponse>>> updateRoomFees(
            @PathVariable Long id, @Valid @RequestBody UpdateRoomFeeRequest request) {
        return ResponseEntity.ok(roomService.updateRoomFees(id, request));
    }

    @PutMapping("/{id}/assets")
    @Operation(summary = "Update room assets", description = "Update the list of mapped assets and their quantities for a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND)
            })
    })
    public ResponseEntity<BaseResponse<String>> updateRoomAssets(
            @PathVariable Long id, @Valid @RequestBody UpdateRoomAssetRequest request) {
        return ResponseEntity.ok(roomService.updateRoomAssets(id, request));
    }

    @PostMapping("/")
    @Operation(summary = "Create a new room", description = "Create a new room with the provided details. The room will be created with is_immutable=false by default")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a room", description = "Soft delete a room by setting is_deleted=true. Only rooms with is_immutable=false can be deleted")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS),
            @J2NApiResponse(httpCode = 400, description = BaseMessageEnum.BaseMessageConstants.BAD_REQUEST, examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROOM_IS_IMMUTABLE)
            })
    })
    public ResponseEntity<BaseResponse<String>> deleteRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }
}
