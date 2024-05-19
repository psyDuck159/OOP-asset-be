package ptit.oop.assetmanagement.mappers;

import ptit.oop.assetmanagement.dtos.AssetDto;
import ptit.oop.assetmanagement.entities.AssetEntity;

import java.util.List;
import java.util.stream.Collectors;

import java.io.Serializable;

public class AssetMapper {
    private AssetMapper() {
    }

    public static AssetDto toDto(AssetEntity entity) {
        return AssetDto.builder()
                .assetCode(entity.getAssetCode())
                .name(entity.getName())
                .specification(entity.getSpecification())
                .installedDate(entity.getInstalledDate())
                .state(entity.getState())
                .category(CategoryMapper.toDto(entity.getCategory()))
                .location(LocationMapper.toDto(entity.getLocation()))
                .build();
    }

    public static AssetEntity toEntity(AssetDto dto) {
        return AssetEntity.builder()
                .assetCode(dto.getAssetCode())
                .name(dto.getName())
                .specification(dto.getSpecification())
                .installedDate(dto.getInstalledDate())
                .state(dto.getState())
                .category(CategoryMapper.toEntity(dto.getCategory()))
                .location(LocationMapper.toEntity(dto.getLocation()))
                .build();
    }


    public static AssetDto toBriefDto(AssetEntity entity) {
        return AssetDto.builder()
                .assetCode(entity.getAssetCode())
                .category(CategoryMapper.toDto(entity.getCategory()))
                .name(entity.getName())
                .state(entity.getState())
                .build();
    }

    public static AssetDto toDetailedDto(AssetEntity entity) {
        return AssetDto.builder()
                .assetCode(entity.getAssetCode())
                .category(CategoryMapper.toDto(entity.getCategory()))
                .location(LocationMapper.toDto(entity.getLocation()))
                .installedDate(entity.getInstalledDate())
                .name(entity.getName())
                .specification(entity.getSpecification())
                .state(entity.getState())
                .assignments(AssignmentMapper.toAssignmentHistory(entity.getAssignments()))
                .build();
    }

    public static List<Serializable> toDtoList(List<AssetEntity> entities) {
        return entities.stream().map(AssetMapper::toBriefDto).collect(Collectors.toList());
    }
}
