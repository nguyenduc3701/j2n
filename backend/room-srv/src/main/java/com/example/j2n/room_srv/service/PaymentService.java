package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.room_srv.constant.BillStatus;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.room_srv.repository.BillRepository;
import com.example.j2n.room_srv.repository.entity.BillEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.j2n.room_srv.constant.MessageEnum;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BillRepository billRepository;

    @Transactional
    @LogAround(message = "Initiate payment for bill")
    public BaseResponse<Object> initiatePayment(String billId) {
        BillEntity bill = billRepository.findById(billId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.BILL_NOT_FOUND.withArgs(billId)));

        if (BillStatus.PAID.equals(bill.getStatus())) {
            return ResponseFactory.error(BaseMessageEnum.BAD_REQUEST);
        }

        return ResponseFactory.success("Payment initiated successfully. Please proceed to payment.");
    }
}
