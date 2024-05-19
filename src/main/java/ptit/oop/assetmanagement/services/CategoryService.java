package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.dtos.CategoryDto;
import ptit.oop.assetmanagement.entities.CategoryEntity;
import ptit.oop.assetmanagement.mappers.CategoryMapper;
import ptit.oop.assetmanagement.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<CategoryDto> getAll() {
		return CategoryMapper.toDtoList(this.categoryRepository.findAll());
	}

	public CategoryDto findOne(String prefix) {
		return CategoryMapper.toDto(this.categoryRepository.findById(prefix).orElse(new CategoryEntity()));
	}

	public CategoryDto create(CategoryDto category) {
		CategoryDto newCategory = CategoryDto.builder().prefix(category.getPrefix().trim()).category(category.getCategory().trim()).build();
		return CategoryMapper.toDto(this.categoryRepository.save(CategoryMapper.toEntity(newCategory)));
	}

	public boolean checkExistenceByPrefix(String prefix) {
		return this.categoryRepository.existsById(prefix);
	}

	public boolean checkExistenceByCategory(String category) {
		return (this.categoryRepository.findFirstByCategory(category) != null);
	}
}
