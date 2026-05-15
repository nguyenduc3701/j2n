package com.example.j2n.bff_srv.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.bff_srv.controller.request.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RestClientUtil restClientUtil;

    // --- Room methods ---
    public Object searchRooms(SearchRoomsRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_SEARCH_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getRoomById(Long id) {
        String path = String.format(GatewayPath.ROOM_DETAIL_PATH, id);
        return restClientUtil.request(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateRoom(Long id, RoomRequest request) {
        String path = String.format(GatewayPath.ROOM_UPDATE_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateRoomFees(Long id, UpdateRoomFeeRequest request) {
        String path = String.format(GatewayPath.ROOM_UPDATE_FEES_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    // --- Asset methods ---
    public Object searchAssets(SearchAssetsRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_ASSET_SEARCH_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object createAsset(CreateAssetRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_ASSET_BASE_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateAsset(Long id, UpdateAssetRequest request) {
        String path = String.format(GatewayPath.ROOM_ASSET_ID_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object deleteAsset(Long id) {
        String path = String.format(GatewayPath.ROOM_ASSET_ID_PATH, id);
        return restClientUtil.request(path, HttpMethod.DELETE, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object mapAssetWithRoom(MapAssetToRoomRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_ASSET_MAP_ROOM_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    // --- Bill methods ---
    public Object searchBills(SearchBillsRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_SEARCH_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object calculateBill(BillRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_CALCULATE_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object calculateAllBills(Integer month) {
        String path = GatewayPath.ROOM_BILL_CALCULATE_ALL_PATH;
        if (month != null) {
            path += "?month=" + month;
        }
        return restClientUtil.request(path, HttpMethod.POST, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object getBillsByRoomId(Long roomId) {
        String path = String.format(GatewayPath.ROOM_BILL_BY_ROOM_ID_PATH, roomId);
        return restClientUtil.request(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object payBill(String billId) {
        String path = String.format(GatewayPath.ROOM_BILL_PAY_PATH, billId);
        return restClientUtil.request(path, HttpMethod.POST, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    // --- Fee methods ---
    public Object getActiveFees() {
        return restClientUtil.request(GatewayPath.ROOM_FEE_BASE_PATH, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateFee(Long id, UpdateFeeRequest request) {
        String path = String.format(GatewayPath.ROOM_FEE_ID_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }
}
