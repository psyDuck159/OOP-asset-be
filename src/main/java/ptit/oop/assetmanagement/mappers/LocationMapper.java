package ptit.oop.assetmanagement.mappers;

import ptit.oop.assetmanagement.dtos.LocationDto;
import ptit.oop.assetmanagement.entities.LocationEntity;

public class LocationMapper {
    private LocationMapper() {}

    public static LocationDto toDto(LocationEntity entity) {
        return new LocationDto(entity.getId(), entity.getName());
    }

    public static LocationEntity toEntity(LocationDto dto) {
        return new LocationEntity(dto.getId(), dto.getName());
    }
}
