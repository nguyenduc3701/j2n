package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.*;
import com.example.j2n.room_srv.controller.response.AssetResponse;
import com.example.j2n.room_srv.controller.response.SearchAssetsResponse;
import com.example.j2n.room_srv.repository.AssetRepository;
import com.example.j2n.room_srv.repository.entity.AssetEntity;
import com.example.j2n.room_srv.repository.entity.RoomAssetEntity;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.utils.PageUtil;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.SearchFactory;
import com.example.j2n.utils.SearchPredicateBuilder.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.LIKE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final RoomService roomService;
    private final RoomAssetService roomAssetService;
    private final SearchFactory searchFactory;

    @LogAround(message = "Search assets")
    public BaseResponse<SearchAssetsResponse> searchAssets(SearchAssetsRequest request) {
        log.info("Searching assets with request: {}", request);
        List<SearchCriteria> criteriaList = buildSearchCriteria(request);
        Page<AssetResponse> pageData = searchFactory.searchAndMap(
                assetRepository,
                criteriaList,
                request,
                this::mapToResponse);

        SearchAssetsResponse response = SearchAssetsResponse.builder()
                .assets(pageData.getContent())
                .page(PageUtil.buildPagingMeta(pageData))
                .build();
        return ResponseFactory.success(response);
    }

    @Transactional
    @LogAround(message = "Create new asset")
    public BaseResponse<AssetResponse> createAsset(CreateAssetRequest request) {
        log.info("Creating new asset with name: {}", request.getName());
        validateAssetNameUnique(request.getName());
        AssetEntity entity = AssetEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return ResponseFactory.success(mapToResponse(assetRepository.save(entity)));
    }

    @Transactional
    @LogAround(message = "Update asset")
    public BaseResponse<AssetResponse> updateAsset(Long id, UpdateAssetRequest request) {
        log.info("Updating asset with id: {}", id);
        AssetEntity entity = findByIdOrThrow(id);
        request.getName().ifPresent(name -> validateAssetNameUnique(name, entity.getId()));
        updateEntityFromRequest(entity, request);
        return ResponseFactory.success(mapToResponse(assetRepository.save(entity)));
    }

    @Transactional
    @LogAround(message = "Delete asset")
    public BaseResponse<Void> deleteAsset(Long id) {
        log.info("Deleting asset with id: {}", id);
        AssetEntity entity = findByIdOrThrow(id);
        assetRepository.delete(entity);
        return ResponseFactory.success(null);
    }

    @Transactional
    @LogAround(message = "Map assets with room")
    public BaseResponse<String> mapAssetWithRoom(MapAssetToRoomRequest request) {
        log.info("Mapping asset ids: {} to room id: {}", request.getAssetIds(), request.getRoomId());
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());

        List<AssetEntity> assets = assetRepository.findAllById(request.getAssetIds());
        if (assets.size() != request.getAssetIds().size()) {
            log.error("Some assets not found. Requested: {}, Found: {}", request.getAssetIds().size(), assets.size());
            throw new DataNotFoundException(MessageEnum.ASSET_NOT_FOUND.withArgs("multiple IDs"));
        }

        List<RoomAssetEntity> assetsToSave = assets.stream()
                .map(asset -> RoomAssetEntity.builder()
                        .room(room)
                        .asset(asset)
                        .build())
                .collect(java.util.stream.Collectors.toList());

        roomAssetService.saveAll(assetsToSave);
        return ResponseFactory.success(MessageEnum.MAP_ASSET_TO_ROOM_SUCCESS.getMessage());
    }

    public AssetEntity findByIdOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ASSET_NOT_FOUND.withArgs(id)));
    }

    private AssetResponse mapToResponse(AssetEntity entity) {
        return AssetResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private List<SearchCriteria> buildSearchCriteria(SearchAssetsRequest request) {
        log.info("Building search criteria for request: {}", request);
        List<SearchCriteria> criteriaList = new ArrayList<>();
        request.getName().ifPresent(name -> criteriaList.add(SearchCriteria.builder()
                .fieldName("name")
                .value(name)
                .operation(LIKE)
                .build()));
        request.getDescription().ifPresent(desc -> criteriaList.add(SearchCriteria.builder()
                .fieldName("description")
                .value(desc)
                .operation(LIKE)
                .build()));
        return criteriaList;
    }

    private void updateEntityFromRequest(AssetEntity entity, UpdateAssetRequest request) {
        log.info("Updating entity from request: {}", request);
        request.getName().ifPresent(entity::setName);
        request.getDescription().ifPresent(entity::setDescription);
    }

    private void validateAssetNameUnique(String name) {
        validateAssetNameUnique(name, null);
    }

    private void validateAssetNameUnique(String name, Long currentId) {
        log.info("Validating asset name unique: {}", name);
        assetRepository.findByName(name).ifPresent(asset -> {
            if (currentId == null || !asset.getId().equals(currentId)) {
                log.error("Asset name already exists: {}", name);
                throw new InvalidInputException(MessageEnum.ASSET_ALREADY_EXISTS.withArgs(name));
            }
        });
    }
}
