package com.example.j2n.payment_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.payment_srv.repository.TransactionRepository;
import com.example.j2n.payment_srv.repository.entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getAllTransactions_shouldReturnBaseResponse() {
        // Arrange
        TransactionEntity t1 = TransactionEntity.builder().id("1").userId("user1").build();
        TransactionEntity t2 = TransactionEntity.builder().id("2").userId("user2").build();
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Act
        BaseResponse<List<TransactionEntity>> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals("user1", result.getData().get(0).getUserId());
    }
}
