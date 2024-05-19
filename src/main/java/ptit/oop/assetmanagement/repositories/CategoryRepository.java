package ptit.oop.assetmanagement.repositories;

import ptit.oop.assetmanagement.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
	CategoryEntity findFirstByCategory(String category);
}
