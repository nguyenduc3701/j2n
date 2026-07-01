package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomFeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomFeeRepository extends JpaRepository<RoomFeeEntity, Long> {
    List<RoomFeeEntity> findByRoomId(Long roomId);

    void deleteByRoomId(Long roomId);

    void deleteByFeeId(Long feeId);
}
