package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.*;
import com.example.j2n.bff_srv.service.RoomService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/rooms")
@RequiredArgsConstructor
@Tag(name = "BFF Room Controller", description = "BFF Endpoints for managing rooms, assets, bills, and fees")
public class RoomController {

    private final RoomService roomService;

    // --- Room Endpoints ---

    @PostMapping("/search")
    @Operation(summary = "Search rooms", description = "Search for rooms based on various criteria")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> searchRooms(@Valid @RequestBody SearchRoomsRequest request) {
        return ResponseEntity.ok(roomService.searchRooms(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieve detailed information about a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Update existing room information")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PutMapping("/{id}/fees")
    @Operation(summary = "Update room fees", description = "Update fee configurations for a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateRoomFees(@PathVariable Long id, @Valid @RequestBody UpdateRoomFeeRequest request) {
        return ResponseEntity.ok(roomService.updateRoomFees(id, request));
    }

    @PutMapping("/{id}/assets")
    @Operation(summary = "Update room assets", description = "Update asset configurations for a specific room in bulk")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateRoomAssets(@PathVariable Long id, @Valid @RequestBody UpdateRoomAssetRequest request) {
        return ResponseEntity.ok(roomService.updateRoomAssets(id, request));
    }

    @PostMapping("/")
    @Operation(summary = "Create a new room", description = "Create a new room with the provided details. The room will be created with is_immutable=false by default")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a room", description = "Soft delete a room by setting is_deleted=true. Only rooms with is_immutable=false can be deleted")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> deleteRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }

    // --- Asset Endpoints ---

    @PostMapping("/assets/search")
    @Operation(summary = "Search assets", description = "Search for asset types")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> searchAssets(@Valid @RequestBody SearchAssetsRequest request) {
        return ResponseEntity.ok(roomService.searchAssets(request));
    }

    @PostMapping("/assets")
    @Operation(summary = "Create asset", description = "Create a new asset type")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        return ResponseEntity.ok(roomService.createAsset(request));
    }

    @PutMapping("/assets/{id}")
    @Operation(summary = "Update asset", description = "Update asset type details")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateAsset(@PathVariable Long id, @Valid @RequestBody UpdateAssetRequest request) {
        return ResponseEntity.ok(roomService.updateAsset(id, request));
    }

    @DeleteMapping("/assets/{id}")
    @Operation(summary = "Delete asset", description = "Delete an asset type")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> deleteAsset(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.deleteAsset(id));
    }

    // --- Bill Endpoints ---

    @PostMapping("/bills/search")
    @Operation(summary = "Search bills", description = "Search for bills matching criteria")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> searchBills(@Valid @RequestBody SearchBillsRequest request) {
        return ResponseEntity.ok(roomService.searchBills(request));
    }

    @PostMapping("/bills/admin/search")
    @Operation(summary = "Search all bills (Admin)", description = "Search for all bills matching criteria (admin)")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> searchBillsAdmin(@Valid @RequestBody SearchBillsAdminRequest request) {
        return ResponseEntity.ok(roomService.searchBillsAdmin(request));
    }

    @PostMapping("/bills/calculate")
    @Operation(summary = "Calculate bill", description = "Calculate and create a monthly bill for a room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> calculateBill(@Valid @RequestBody BillRequest request) {
        return ResponseEntity.ok(roomService.calculateBill(request));
    }

    @PostMapping("/bills/calculate-all")
    @Operation(summary = "Calculate all bills", description = "Generate bills for all occupied rooms for a specific month using the provided new electricity indices")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> calculateAllBills(@Valid @RequestBody CalculateAllBillsRequest request) {
        return ResponseEntity.ok(roomService.calculateAllBills(request));
    }

    @GetMapping("/bills/room/{roomId}")
    @Operation(summary = "Get room bills", description = "Retrieve all bills for a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> getBillsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getBillsByRoomId(roomId));
    }

    @PostMapping("/bills/{billId}/pay")
    @Operation(summary = "Pay bill", description = "Initiate payment for a specific bill")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> payBill(@PathVariable String billId) {
        return ResponseEntity.ok(roomService.payBill(billId));
    }

    // --- Fee Endpoints ---

    @GetMapping("/fees")
    @Operation(summary = "Get active fees", description = "Retrieve all currently active fee types")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> getActiveFees() {
        return ResponseEntity.ok(roomService.getActiveFees());
    }

    @PostMapping("/fees")
    @Operation(summary = "Create fee", description = "Create a new fee type")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> createFee(@Valid @RequestBody CreateFeeRequest request) {
        return ResponseEntity.ok(roomService.createFee(request));
    }

    @PutMapping("/fees/{id}")
    @Operation(summary = "Update fee", description = "Update fee type configuration")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateFee(@PathVariable Long id, @Valid @RequestBody UpdateFeeRequest request) {
        return ResponseEntity.ok(roomService.updateFee(id, request));
    }

    // --- Member Endpoints ---

    @GetMapping("/members/room/{roomId}")
    @Operation(summary = "Get room members by room id", description = "Get all members mapped to a specific room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> getRoomMembersByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomMembersByRoomId(roomId));
    }

    @PostMapping("/members/mapping")
    @Operation(summary = "Map members to room", description = "Assign users as members of a room")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> mapMemberToRoom(@Valid @RequestBody MapMemberToRoomRequest request) {
        return ResponseEntity.ok(roomService.mapMemberToRoom(request));
    }

    @PutMapping("/members/{userId}")
    @Operation(summary = "Update room member by user id", description = "Update room member attributes like primary status by user ID")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> updateRoomMemberByUserId(@PathVariable Long userId, @Valid @RequestBody UpdateRoomMemberRequest request) {
        return ResponseEntity.ok(roomService.updateRoomMemberByUserId(userId, request));
    }

    @DeleteMapping("/members/user/{userId}")
    @Operation(summary = "Delete room member by user id", description = "Remove a member from a room by user ID")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    public ResponseEntity<Object> deleteRoomMemberByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(roomService.deleteRoomMemberByUserId(userId));
    }
}
