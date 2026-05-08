package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.example.j2n.room_srv.constant.MessageEnum;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BillRepository billRepository;
    private final RestTemplate restTemplate;

    @Value("${api-gateway.order.base-url:http://localhost:8188}")
    private String orderSrvUrl;

    @Transactional
    @LogAround(message = "Initiate payment for bill")
    public BaseResponse<Object> initiatePayment(String billId) {
        BillEntity bill = billRepository.findById(billId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.BILL_NOT_FOUND.withArgs(billId)));

        if ("PAID".equals(bill.getStatus())) {
            return ResponseFactory.error(BaseMessageEnum.BAD_REQUEST);
        }

        // Prepare request to order-srv
        Map<String, Object> request = new HashMap<>();
        request.put("user_id", bill.getRenterId());
        request.put("item_id", bill.getId());
        request.put("item_type", "ROOM");
        request.put("quantity", 1);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("room_number", bill.getRoom().getRoomNumber());
        metadata.put("period", bill.getBillingMonth().toString());
        request.put("metadata", metadata);

        try {
            log.info("[ROOM-SRV] Calling order-srv to create payment item for bill: {}", billId);
            ResponseEntity<BaseResponse> response = restTemplate.postForEntity(
                    orderSrvUrl + "/orders", request, BaseResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // In a real app, you might want to extract the order ID from the response
                // and save it to the bill.
                return ResponseFactory.success("Payment initiated successfully. Please proceed to payment.");
            } else {
                return ResponseFactory.error(BaseMessageEnum.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("[ROOM-SRV] Error calling order-srv", e);
            return ResponseFactory.error(BaseMessageEnum.INTERNAL_ERROR);
        }
    }
}
