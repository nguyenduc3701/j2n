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
        BillRequest request = new BillRequest();
        when(restClientUtil.request(eq(GatewayPath.ROOM_BILL_CALCULATE_PATH), eq(HttpMethod.POST), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.calculateBill(request);

        assertNotNull(result);
    }

    @Test
    void calculateAllBills_WithMonth_Success() {
        Integer month = 5;
        String expectedPath = GatewayPath.ROOM_BILL_CALCULATE_ALL_PATH + "?month=" + month;
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.POST), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.calculateAllBills(month);

        assertNotNull(result);
    }

    @Test
    void calculateAllBills_NullMonth_Success() {
        String expectedPath = GatewayPath.ROOM_BILL_CALCULATE_ALL_PATH;
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.POST), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.calculateAllBills(null);

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
    void updateRoomMember_Success() {
        Long id = 1L;
        UpdateRoomMemberRequest request = new UpdateRoomMemberRequest();
        String expectedPath = String.format(GatewayPath.ROOM_MEMBER_ID_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.updateRoomMember(id, request);

        assertNotNull(result);
    }

    @Test
    void deleteRoomMember_Success() {
        Long id = 1L;
        String expectedPath = String.format(GatewayPath.ROOM_MEMBER_ID_PATH, id);
        when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new Object());

        Object result = roomService.deleteRoomMember(id);

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
}
