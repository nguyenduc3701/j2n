package com.example.j2n.payment_srv.repository;

import com.example.j2n.payment_srv.repository.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Optional<TransactionEntity> findByExternalTxId(String externalTxId);
}
