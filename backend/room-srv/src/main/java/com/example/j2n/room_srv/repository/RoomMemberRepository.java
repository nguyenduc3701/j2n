package com.example.j2n.room_srv.repository;

import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMemberEntity, Long> {
    Optional<RoomMemberEntity> findByRoomIdAndUserId(Long roomId, Long userId);
    List<RoomMemberEntity> findByRoomId(Long roomId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
    Optional<RoomMemberEntity> findByUserId(Long userId);
}
