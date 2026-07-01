package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.*;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RestClientUtil restClientUtil;

    @InjectMocks
    private RoomService roomService;

    @Test
    void searchRooms_Success() {
        SearchRoomsRequest request = new SearchRoomsRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_SEARCH_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.searchRooms(request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(GatewayPath.ROOM_SEARCH_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class));
    }

    @Test
    void getRoomById_Success() {
        Long id = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_DETAIL_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.getRoomById(id);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    void getRoomById_WithEnrichment() {
        Long id = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_DETAIL_PATH, id);
        
        // Prepare mock member data
        Map<String, Object> mockMember = new java.util.HashMap<>();
        mockMember.put("id", 1L);
        mockMember.put("user_id", 10L);
        mockMember.put("room_id", id);
        
        List<Map<String, Object>> memberList = new java.util.ArrayList<>();
        memberList.add(mockMember);
        
        Map<String, Object> mockRoom = new java.util.HashMap<>();
        mockRoom.put("id", id);
        mockRoom.put("members", memberList);

        Map<String, Object> mockResponse = new java.util.HashMap<>();
        mockResponse.put("code", 200);
        mockResponse.put("data", mockRoom);

        // Prepare mock user data
        Map<String, Object> mockUser = new java.util.HashMap<>();
        mockUser.put("id", 10L);
        mockUser.put("full_name", "John Doe");
        mockUser.put("phone_number", "0987654321");
        mockUser.put("email", "john@example.com");
        
        Map<String, Object> mockUserResponse = new java.util.HashMap<>();
        mockUserResponse.put("code", 200);
        mockUserResponse.put("data", mockUser);

        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);
        
        String expectedUserPath = String.format(GatewayPath.AUTH_USER_ID_PATH, "10");
        when(restClientUtil.request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockUserResponse);

        Object result = roomService.getRoomById(id);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
        verify(restClientUtil, times(1)).request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
        
        // Assert that the member inside the room details has been enriched
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", mockMember.get("full_name"));
        org.junit.jupiter.api.Assertions.assertEquals("0987654321", mockMember.get("phone_number"));
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", mockMember.get("email"));
    }

    @Test
    void updateRoom_Success() {
        Long id = 1L;
        RoomRequest request = new RoomRequest();
        String expectedPath = String.format(GatewayPath.ROOM_UPDATE_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateRoom(id, request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class));
    }

    @Test
    void updateRoom_WithEnrichment() {
        Long id = 1L;
        RoomRequest request = new RoomRequest();
        String expectedPath = String.format(GatewayPath.ROOM_UPDATE_PATH, id);
        
        // Prepare mock member data
        Map<String, Object> mockMember = new java.util.HashMap<>();
        mockMember.put("id", 1L);
        mockMember.put("user_id", 10L);
        mockMember.put("room_id", id);
        
        List<Map<String, Object>> memberList = new java.util.ArrayList<>();
        memberList.add(mockMember);
        
        Map<String, Object> mockRoom = new java.util.HashMap<>();
        mockRoom.put("id", id);
        mockRoom.put("members", memberList);

        Map<String, Object> mockResponse = new java.util.HashMap<>();
        mockResponse.put("code", 200);
        mockResponse.put("data", mockRoom);

        // Prepare mock user data
        Map<String, Object> mockUser = new java.util.HashMap<>();
        mockUser.put("id", 10L);
        mockUser.put("full_name", "John Doe");
        mockUser.put("phone_number", "0987654321");
        mockUser.put("email", "john@example.com");
        
        Map<String, Object> mockUserResponse = new java.util.HashMap<>();
        mockUserResponse.put("code", 200);
        mockUserResponse.put("data", mockUser);

        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);
        
        String expectedUserPath = String.format(GatewayPath.AUTH_USER_ID_PATH, "10");
        when(restClientUtil.request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockUserResponse);

        Object result = roomService.updateRoom(id, request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class));
        verify(restClientUtil, times(1)).request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
        
        // Assert that the member inside the updated room details has been enriched
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", mockMember.get("full_name"));
        org.junit.jupiter.api.Assertions.assertEquals("0987654321", mockMember.get("phone_number"));
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", mockMember.get("email"));
    }

    @Test
    void updateRoomFees_Success() {
        Long id = 1L;
        UpdateRoomFeeRequest request = new UpdateRoomFeeRequest();
        String expectedPath = String.format(GatewayPath.ROOM_UPDATE_FEES_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateRoomFees(id, request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class));
    }

    @Test
    void searchAssets_Success() {
        SearchAssetsRequest request = new SearchAssetsRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_ASSET_SEARCH_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.searchAssets(request);

        assertNotNull(result);
    }

    @Test
    void createAsset_Success() {
        CreateAssetRequest request = new CreateAssetRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_ASSET_BASE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.createAsset(request);

        assertNotNull(result);
    }

    @Test
    void updateAsset_Success() {
        Long id = 1L;
        UpdateAssetRequest request = new UpdateAssetRequest();
        String expectedPath = String.format(GatewayPath.ROOM_ASSET_ID_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateAsset(id, request);

        assertNotNull(result);
    }

    @Test
    void deleteAsset_Success() {
        Long id = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_ASSET_ID_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.deleteAsset(id);

        assertNotNull(result);
    }

    @Test
    void searchBills_Success() {
        SearchBillsRequest request = new SearchBillsRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BILL_SEARCH_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.searchBills(request);

        assertNotNull(result);
    }

    @Test
    void searchBillsAdmin_Success() {
        SearchBillsAdminRequest request = new SearchBillsAdminRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BILL_ADMIN_SEARCH_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.searchBillsAdmin(request);

        assertNotNull(result);
    }

    @Test
    void calculateBill_Success() {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .electricityNewIndex(1000)
                .build();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BILL_CALCULATE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.calculateBill(request);

        assertNotNull(result);
    }

    @Test
    void calculateAllBills_Success() {
        CalculateAllBillsRequest request = CalculateAllBillsRequest.builder()
                .month(5)
                .electricIndices(java.util.Collections.singletonMap(1L, 100))
                .build();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BILL_CALCULATE_ALL_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.calculateAllBills(request);

        assertNotNull(result);
    }

    @Test
    void getBillsByRoomId_Success() {
        Long roomId = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_BILL_BY_ROOM_ID_PATH, roomId);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.getBillsByRoomId(roomId);

        assertNotNull(result);
    }

    @Test
    void payBill_Success() {
        String billId = "bill-123";
        String expectedPath = String.format(GatewayPath.ROOM_BILL_PAY_PATH, billId);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.POST), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.payBill(billId);

        assertNotNull(result);
    }

    @Test
    void getActiveFees_Success() {
        when(restClientUtil.request(eq(GatewayPath.ROOM_FEE_BASE_PATH), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.getActiveFees();

        assertNotNull(result);
    }

    @Test
    void createFee_Success() {
        CreateFeeRequest request = CreateFeeRequest.builder()
                .name("Electricity")
                .unitPrice(BigDecimal.valueOf(3500))
                .unitName("kWh")
                .build();
        when(restClientUtil.request(eq(GatewayPath.ROOM_FEE_BASE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.createFee(request);

        assertNotNull(result);
    }

    @Test
    void updateFee_Success() {
        Long id = 1L;
        UpdateFeeRequest request = UpdateFeeRequest.builder()
                .name(Optional.of("Water"))
                .build();
        String expectedPath = String.format(GatewayPath.ROOM_FEE_ID_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateFee(id, request);

        assertNotNull(result);
    }

    @Test
    void getRoomMembersByRoomId_Success() {
        Long roomId = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_MEMBER_BY_ROOM_ID_PATH, roomId);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.getRoomMembersByRoomId(roomId);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    void mapMemberToRoom_Success() {
        MapMemberToRoomRequest request = new MapMemberToRoomRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_MEMBER_MAP_ROOM_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.mapMemberToRoom(request);

        assertNotNull(result);
    }

    @Test
    void updateRoomMemberByUserId_Success() {
        Long userId = 1L;
        UpdateRoomMemberRequest request = new UpdateRoomMemberRequest();
        String expectedPath = String.format(GatewayPath.ROOM_MEMBER_ID_PATH, userId);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateRoomMemberByUserId(userId, request);

        assertNotNull(result);
    }

    @Test
    void deleteRoomMemberByUserId_Success() {
        Long userId = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_MEMBER_BY_USER_ID_PATH, userId);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.deleteRoomMemberByUserId(userId);

        assertNotNull(result);
    }

    @Test
    void updateRoomAssets_Success() {
        Long id = 1L;
        UpdateRoomAssetRequest request = new UpdateRoomAssetRequest();
        String expectedPath = String.format(GatewayPath.ROOM_UPDATE_ASSETS_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateRoomAssets(id, request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class));
    }

    @Test
    void createRoom_Success() {
        CreateRoomRequest request = new CreateRoomRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BASE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.createRoom(request);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(GatewayPath.ROOM_BASE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class));
    }

    @Test
    void deleteRoom_Success() {
        Long id = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_DELETE_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.deleteRoom(id);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class));
    }

    @Test
    void getRoomMembersByRoomId_WithEnrichment() {
        Long roomId = 1L;
        String expectedMemberPath = String.format(GatewayPath.ROOM_MEMBER_BY_ROOM_ID_PATH, roomId);
        
        // Prepare mock member data
        Map<String, Object> mockMember = new java.util.HashMap<>();
        mockMember.put("id", 1L);
        mockMember.put("user_id", 10L);
        mockMember.put("room_id", roomId);
        
        List<Map<String, Object>> memberList = new java.util.ArrayList<>();
        memberList.add(mockMember);
        
        Map<String, Object> mockResponse = new java.util.HashMap<>();
        mockResponse.put("code", 200);
        mockResponse.put("data", memberList);

        // Prepare mock user data
        Map<String, Object> mockUser = new java.util.HashMap<>();
        mockUser.put("id", 10L);
        mockUser.put("full_name", "John Doe");
        mockUser.put("phone_number", "0987654321");
        mockUser.put("email", "john@example.com");
        
        Map<String, Object> mockUserResponse = new java.util.HashMap<>();
        mockUserResponse.put("code", 200);
        mockUserResponse.put("data", mockUser);

        when(restClientUtil.request(eq(expectedMemberPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);
        
        String expectedUserPath = String.format(GatewayPath.AUTH_USER_ID_PATH, "10");
        when(restClientUtil.request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockUserResponse);

        Object result = roomService.getRoomMembersByRoomId(roomId);

        assertNotNull(result);
        verify(restClientUtil, times(1)).request(eq(expectedMemberPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
        verify(restClientUtil, times(1)).request(eq(expectedUserPath), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class));
        
        // Assert that the member has been enriched
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", mockMember.get("full_name"));
        org.junit.jupiter.api.Assertions.assertEquals("0987654321", mockMember.get("phone_number"));
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", mockMember.get("email"));
    }
}
