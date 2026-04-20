package com.example.j2n.payment_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.TransactionRepository;
import com.example.j2n.payment_srv.repository.entity.TransactionEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import com.example.j2n.aspect.LogAround;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PayOS payOS;

    @LogAround(message = "Get all transactions")
    public BaseResponse<List<TransactionEntity>> getAllTransactions() {
        return ResponseFactory.success(transactionRepository.findAll());
    }
}
