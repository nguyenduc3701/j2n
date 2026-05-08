package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomObjectRepository extends JpaRepository<RoomObjectEntity, Long> {
    List<RoomObjectEntity> findByRoomId(Long roomId);
}
