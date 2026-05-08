package com.example.j2n.order_srv.repository;

import com.example.j2n.order_srv.repository.entity.RoomBillInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomBillInfoRepository extends JpaRepository<RoomBillInfoEntity, String> {
}
