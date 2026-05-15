package com.example.j2n.room_srv.service;

import com.example.j2n.utils.ResponseFactory;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.BillStatus;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePayment_Success() {
        String billId = "bill-1";
        RoomEntity room = RoomEntity.builder().roomNumber("101").build();
        BillEntity bill = BillEntity.builder()
                .id(billId)
                .renterId(123L)
                .room(room)
                .billingMonth(5)
                .status(BillStatus.UNPAID)
                .build();

        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));

        BaseResponse mockResponse = ResponseFactory.success("Success");
        when(restTemplate.postForEntity(anyString(), any(), eq(BaseResponse.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        BaseResponse<Object> response = paymentService.initiatePayment(billId);

        assertNotNull(response);
        assertEquals("200", response.getCode());
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(BaseResponse.class));
    }

    @Test
    void initiatePayment_AlreadyPaid() {
        String billId = "bill-1";
        BillEntity bill = BillEntity.builder().id(billId).status(BillStatus.PAID).build();

        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));

        BaseResponse<Object> response = paymentService.initiatePayment(billId);

        assertEquals("400", response.getCode());
    }

    @Test
    void initiatePayment_BillNotFound() {
        when(billRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> paymentService.initiatePayment("invalid"));
    }

    @Test
    void initiatePayment_ErrorCallingOrderSrv() {
        String billId = "bill-1";
        RoomEntity room = RoomEntity.builder().roomNumber("101").build();
        BillEntity bill = BillEntity.builder()
                .id(billId)
                .renterId(123L)
                .room(room)
                .billingMonth(5)
                .status(BillStatus.UNPAID)
                .build();

        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));
        when(restTemplate.postForEntity(anyString(), any(), eq(BaseResponse.class)))
                .thenThrow(new RuntimeException("API Down"));

        BaseResponse<Object> response = paymentService.initiatePayment(billId);

        assertEquals("500", response.getCode());
    }
}
