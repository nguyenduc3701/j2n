package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, String>, JpaSpecificationExecutor<BillEntity> {
    List<BillEntity> findByRoomId(Long roomId);
    List<BillEntity> findByRoomIdAndBillingMonth(Long roomId, Integer month);
}
