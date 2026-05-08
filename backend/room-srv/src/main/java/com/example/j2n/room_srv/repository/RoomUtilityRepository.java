package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomUtilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomUtilityRepository extends JpaRepository<RoomUtilityEntity, Long> {
    List<RoomUtilityEntity> findByRoomId(Long roomId);

    void deleteByRoomId(Long roomId);
}
