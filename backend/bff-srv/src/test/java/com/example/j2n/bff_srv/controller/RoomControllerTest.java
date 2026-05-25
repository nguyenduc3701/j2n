package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.*;
import com.example.j2n.bff_srv.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/bff/rooms";

    // --- Fee Endpoints Tests ---

    @Test
    void getActiveFees_ShouldReturnSuccess() throws Exception {
        when(roomService.getActiveFees()).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(get(BASE_URL + "/fees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(roomService, times(1)).getActiveFees();
    }

    @Test
    void createFee_ShouldReturnSuccess() throws Exception {
        CreateFeeRequest request = CreateFeeRequest.builder()
                .name("Electricity")
                .unitPrice(BigDecimal.valueOf(3500))
                .unitName("kWh")
                .build();

        when(roomService.createFee(any(CreateFeeRequest.class))).thenReturn(Collections.singletonMap("data", "created"));

        mockMvc.perform(post(BASE_URL + "/fees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).createFee(any(CreateFeeRequest.class));
    }

    @Test
    void updateFee_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        UpdateFeeRequest request = UpdateFeeRequest.builder()
                .name(Optional.of("Water"))
                .build();

        when(roomService.updateFee(eq(id), any(UpdateFeeRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/fees/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).updateFee(eq(id), any(UpdateFeeRequest.class));
    }

    // --- Room Endpoints Tests ---

    @Test
    void searchRooms_ShouldReturnSuccess() throws Exception {
        SearchRoomsRequest request = new SearchRoomsRequest();
        when(roomService.searchRooms(any(SearchRoomsRequest.class))).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(post(BASE_URL + "/search")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getRoomById_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        when(roomService.getRoomById(id)).thenReturn(Collections.singletonMap("data", "detail"));

        mockMvc.perform(get(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateRoom_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        RoomRequest request = RoomRequest.builder()
                .roomNumber(Optional.of("101"))
                .build();
        when(roomService.updateRoom(eq(id), any(RoomRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRoomFees_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        UpdateRoomFeeRequest request = UpdateRoomFeeRequest.builder()
                .feeIds(List.of(1, 2))
                .build();
        when(roomService.updateRoomFees(eq(id), any(UpdateRoomFeeRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/{id}/fees", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRoomAssets_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        UpdateRoomAssetRequest request = UpdateRoomAssetRequest.builder()
                .assets(List.of(UpdateRoomAssetRequest.AssetMapping.builder()
                        .assetId(2L)
                        .quantity(5)
                        .build()))
                .build();
        when(roomService.updateRoomAssets(eq(id), any(UpdateRoomAssetRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/{id}/assets", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- Asset Endpoints Tests ---

    @Test
    void searchAssets_ShouldReturnSuccess() throws Exception {
        SearchAssetsRequest request = new SearchAssetsRequest();
        when(roomService.searchAssets(any(SearchAssetsRequest.class))).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(post(BASE_URL + "/assets/search")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createAsset_ShouldReturnSuccess() throws Exception {
        CreateAssetRequest request = CreateAssetRequest.builder()
                .name("Water Heater")
                .build();
        when(roomService.createAsset(any(CreateAssetRequest.class))).thenReturn(Collections.singletonMap("data", "created"));

        mockMvc.perform(post(BASE_URL + "/assets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAsset_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        UpdateAssetRequest request = UpdateAssetRequest.builder()
                .name(Optional.of("New Asset"))
                .build();
        when(roomService.updateAsset(eq(id), any(UpdateAssetRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/assets/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAsset_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        when(roomService.deleteAsset(id)).thenReturn(Collections.singletonMap("data", "deleted"));

        mockMvc.perform(delete(BASE_URL + "/assets/{id}", id)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    // --- Bill Endpoints Tests ---

    @Test
    void searchBills_ShouldReturnSuccess() throws Exception {
        SearchBillsRequest request = new SearchBillsRequest();
        request.setRoomId(1L);
        when(roomService.searchBills(any(SearchBillsRequest.class))).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(post(BASE_URL + "/bills/search")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void searchBillsAdmin_ShouldReturnSuccess() throws Exception {
        SearchBillsAdminRequest request = new SearchBillsAdminRequest();
        when(roomService.searchBillsAdmin(any(SearchBillsAdminRequest.class))).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(post(BASE_URL + "/bills/admin/search")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void calculateBill_ShouldReturnSuccess() throws Exception {
        BillRequest request = BillRequest.builder()
                .roomId(1L)
                .build();
        when(roomService.calculateBill(any(BillRequest.class))).thenReturn(Collections.singletonMap("data", "calculated"));

        mockMvc.perform(post(BASE_URL + "/bills/calculate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void calculateAllBills_ShouldReturnSuccess() throws Exception {
        Integer month = 5;
        when(roomService.calculateAllBills(month)).thenReturn(Collections.singletonMap("data", "calculated"));

        mockMvc.perform(post(BASE_URL + "/bills/calculate-all")
                .param("month", String.valueOf(month))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getBillsByRoomId_ShouldReturnSuccess() throws Exception {
        Long roomId = 1L;
        when(roomService.getBillsByRoomId(roomId)).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(get(BASE_URL + "/bills/room/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void payBill_ShouldReturnSuccess() throws Exception {
        String billId = "bill-123";
        when(roomService.payBill(billId)).thenReturn(Collections.singletonMap("data", "paid"));

        mockMvc.perform(post(BASE_URL + "/bills/{billId}/pay", billId)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    // --- Member Endpoints Tests ---

    @Test
    void getRoomMembersByRoomId_ShouldReturnSuccess() throws Exception {
        Long roomId = 1L;
        when(roomService.getRoomMembersByRoomId(roomId)).thenReturn(Collections.singletonMap("data", "list"));

        mockMvc.perform(get(BASE_URL + "/members/room/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(roomService, times(1)).getRoomMembersByRoomId(roomId);
    }

    @Test
    void mapMemberToRoom_ShouldReturnSuccess() throws Exception {
        MapMemberToRoomRequest request = MapMemberToRoomRequest.builder()
                .roomId(1L)
                .userIds(List.of(10L))
                .build();
        when(roomService.mapMemberToRoom(any(MapMemberToRoomRequest.class))).thenReturn(Collections.singletonMap("data", "mapped"));

        mockMvc.perform(post(BASE_URL + "/members/mapping")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateRoomMember_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        UpdateRoomMemberRequest request = UpdateRoomMemberRequest.builder()
                .isPrimary(Optional.of(true))
                .build();
        when(roomService.updateRoomMember(eq(id), any(UpdateRoomMemberRequest.class))).thenReturn(Collections.singletonMap("data", "updated"));

        mockMvc.perform(put(BASE_URL + "/members/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRoomMember_ShouldReturnSuccess() throws Exception {
        Long id = 1L;
        when(roomService.deleteRoomMember(id)).thenReturn(Collections.singletonMap("data", "deleted"));

        mockMvc.perform(delete(BASE_URL + "/members/{id}", id)
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
