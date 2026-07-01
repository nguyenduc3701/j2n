package com.example.j2n.bff_srv.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.bff_srv.controller.request.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

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
        Object response = restClientUtil.request(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
        enrichRoomResponse(response);
        return response;
    }

    public Object updateRoom(Long id, RoomRequest request) {
        String path = String.format(GatewayPath.ROOM_UPDATE_PATH, id);
        Object response = restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
        enrichRoomResponse(response);
        return response;
    }

    public Object updateRoomFees(Long id, UpdateRoomFeeRequest request) {
        String path = String.format(GatewayPath.ROOM_UPDATE_FEES_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateRoomAssets(Long id, UpdateRoomAssetRequest request) {
        String path = String.format(GatewayPath.ROOM_UPDATE_ASSETS_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object createRoom(CreateRoomRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BASE_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object deleteRoom(Long id) {
        String path = String.format(GatewayPath.ROOM_DELETE_PATH, id);
        return restClientUtil.request(path, HttpMethod.DELETE, null,
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

    // --- Bill methods ---
    public Object searchBills(SearchBillsRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_SEARCH_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object searchBillsAdmin(SearchBillsAdminRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_ADMIN_SEARCH_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object calculateBill(BillRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_CALCULATE_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object calculateAllBills(CalculateAllBillsRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_BILL_CALCULATE_ALL_PATH, HttpMethod.POST, request,
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

    public Object createFee(CreateFeeRequest request) {
        return restClientUtil.request(GatewayPath.ROOM_FEE_BASE_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    public Object updateFee(Long id, UpdateFeeRequest request) {
        String path = String.format(GatewayPath.ROOM_FEE_ID_PATH, id);
        return restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
    }

    // --- Member methods ---
    public Object getRoomMembersByRoomId(Long roomId) {
        String path = String.format(GatewayPath.ROOM_MEMBER_BY_ROOM_ID_PATH, roomId);
        Object response = restClientUtil.request(path, HttpMethod.GET, null,
                new ParameterizedTypeReference<Object>() {
                });
        enrichMemberResponse(response);
        return response;
    }

    public Object mapMemberToRoom(MapMemberToRoomRequest request) {
        Object response = restClientUtil.request(GatewayPath.ROOM_MEMBER_MAP_ROOM_PATH, HttpMethod.POST, request,
                new ParameterizedTypeReference<Object>() {
                });
        enrichMemberResponse(response);
        return response;
    }

    public Object updateRoomMemberByUserId(Long userId, UpdateRoomMemberRequest request) {
        String path = String.format(GatewayPath.ROOM_MEMBER_ID_PATH, userId);
        Object response = restClientUtil.request(path, HttpMethod.PUT, request,
                new ParameterizedTypeReference<Object>() {
                });
        enrichMemberResponse(response);
        return response;
    }

    public Object deleteRoomMemberByUserId(Long userId) {
        String path = String.format(GatewayPath.ROOM_MEMBER_BY_USER_ID_PATH, userId);
        return restClientUtil.request(path, HttpMethod.DELETE, null,
                new ParameterizedTypeReference<Object>() {
                });
    }

    @SuppressWarnings("unchecked")
    private void enrichMemberResponse(Object responseObj) {
        if (!(responseObj instanceof Map)) {
            return;
        }
        Map<String, Object> responseMap = (Map<String, Object>) responseObj;
        Object data = responseMap.get("data");
        if (data == null) {
            return;
        }

        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object item : list) {
                if (item instanceof Map) {
                    enrichSingleMember((Map<String, Object>) item);
                }
            }
        } else if (data instanceof Map) {
            enrichSingleMember((Map<String, Object>) data);
        }
    }

    @SuppressWarnings("unchecked")
    private void enrichSingleMember(Map<String, Object> member) {
        Object userIdObj = member.get("user_id");
        if (userIdObj != null) {
            try {
                String userId = userIdObj.toString();
                Object userResponseObj = restClientUtil.request(
                        String.format(GatewayPath.AUTH_USER_ID_PATH, userId),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Object>() {
                        });
                if (userResponseObj instanceof Map) {
                    Map<String, Object> userResponseMap = (Map<String, Object>) userResponseObj;
                    Object userData = userResponseMap.get("data");
                    if (userData instanceof Map) {
                        Map<String, Object> userItem = (Map<String, Object>) userData;
                        member.put("full_name", userItem.get("full_name"));
                        member.put("phone_number", userItem.get("phone_number"));
                        member.put("email", userItem.get("email"));
                    }
                }
            } catch (Exception e) {
                log.error("Failed to enrich user details for userId: " + userIdObj, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void enrichRoomResponse(Object responseObj) {
        if (!(responseObj instanceof Map)) {
            return;
        }
        Map<String, Object> responseMap = (Map<String, Object>) responseObj;
        Object data = responseMap.get("data");
        if (!(data instanceof Map)) {
            return;
        }
        Map<String, Object> roomData = (Map<String, Object>) data;
        Object membersObj = roomData.get("members");
        if (membersObj instanceof List) {
            List<?> list = (List<?>) membersObj;
            for (Object item : list) {
                if (item instanceof Map) {
                    enrichSingleMember((Map<String, Object>) item);
                }
            }
        }
    }
}

