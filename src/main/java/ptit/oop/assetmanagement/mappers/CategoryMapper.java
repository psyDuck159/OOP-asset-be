package ptit.oop.assetmanagement.mappers;

import ptit.oop.assetmanagement.dtos.CategoryDto;
import ptit.oop.assetmanagement.entities.CategoryEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

	private CategoryMapper() {}

	public static CategoryDto toDto(CategoryEntity entity) {
		return CategoryDto.builder().prefix(entity.getPrefix()).category(entity.getCategory()).build();
	}

	public static CategoryEntity toEntity(CategoryDto dto) {
		return new CategoryEntity(dto.getPrefix(), dto.getCategory());
	}

	public static List<CategoryDto> toDtoList(List<CategoryEntity> entities) {
		return entities.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
	}
}
