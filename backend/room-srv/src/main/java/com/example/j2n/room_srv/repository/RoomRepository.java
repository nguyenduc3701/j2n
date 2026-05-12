package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {
    Optional<RoomEntity> findByRoomNumber(String roomNumber);
}
