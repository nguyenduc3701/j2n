package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.room_srv.repository.RoomAssetRepository;
import com.example.j2n.room_srv.repository.entity.RoomAssetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomAssetService {

    private final RoomAssetRepository roomAssetRepository;

    @Transactional
    @LogAround(message = "Save batch room assets")
    public void saveAll(List<RoomAssetEntity> assets) {
        log.info("Saving {} room assets", assets.size());
        roomAssetRepository.saveAll(assets);
    }

    @LogAround(message = "Get assets by room ID")
    public List<RoomAssetEntity> getAssetsByRoomId(Long roomId) {
        return roomAssetRepository.findByRoomId(roomId);
    }
}
