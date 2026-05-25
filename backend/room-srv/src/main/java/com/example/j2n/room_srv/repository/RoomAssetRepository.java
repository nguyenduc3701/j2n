package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAssetRepository extends JpaRepository<RoomAssetEntity, Long> {
    List<RoomAssetEntity> findByRoomId(Long roomId);
    Optional<RoomAssetEntity> findByRoomIdAndAssetId(Long roomId, Long assetId);
}
