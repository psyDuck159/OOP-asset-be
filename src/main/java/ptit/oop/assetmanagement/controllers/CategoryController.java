package ptit.oop.assetmanagement.controllers;

import ptit.oop.assetmanagement.dtos.CategoryDto;
import ptit.oop.assetmanagement.dtos.ResponseObject;
import ptit.oop.assetmanagement.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@PreAuthorize("hasAuthority('Admin')")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<ResponseObject> getCategories() {
		return ResponseEntity.ok(
				ResponseObject.builder()
						.status(HttpStatus.OK)
						.message("Get all categories in system successfully!")
						.data(this.categoryService.getAll())
						.build());
	}

	@GetMapping("/{prefix}")
	public ResponseEntity<ResponseObject> getCategory(@PathVariable String prefix) {
		return ResponseEntity.ok(
				ResponseObject.builder()
						.status(HttpStatus.OK)
						.message("Get all categories in system successfully!")
						.data(this.categoryService.findOne(prefix))
						.build());
	}

	@PostMapping
	public ResponseEntity<ResponseObject> createCategory(@RequestBody CategoryDto category) {
		if (category.getPrefix().trim().length() != 2) {
			return ResponseEntity.badRequest().build();
		}

		if (category.getCategory().trim().length() < 1) {
			return ResponseEntity.badRequest().build();
		}

		if (this.categoryService.checkExistenceByPrefix(category.getPrefix()) || this.categoryService.checkExistenceByCategory(category.getCategory())) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(
				ResponseObject.builder()
						.status(HttpStatus.CREATED)
						.message("Get all categories in system successfully!")
						.data(this.categoryService.create(category))
						.build());
	}

	@GetMapping("/exist-prefix/{prefix}")
	public ResponseEntity<Boolean> checkExistByPrefix(@PathVariable String prefix) {
		return ResponseEntity.status(HttpStatus.OK).body(this.categoryService.checkExistenceByPrefix(prefix));
	}

	@GetMapping("/exist-category/{category}")
	public ResponseEntity<Boolean> checkExistByCategory(@PathVariable String category) {
		return ResponseEntity.status(HttpStatus.OK).body(this.categoryService.checkExistenceByCategory(category));
	}
}
