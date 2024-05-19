package ptit.oop.assetmanagement.repositories;

import ptit.oop.assetmanagement.entities.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, String> {
	AssetEntity findFirstByAssetCodeContainsOrderByAssetCodeDesc(String prefix);
    @Query(value = "SELECT * FROM assets a " +
            "JOIN categories c ON a.category_id = c.prefix " +
            "WHERE a.location_id = :location AND a.state IN ('Available', 'Not available', 'Assigned')",
            countQuery = "SELECT COUNT(*) FROM assets a " +
                    "JOIN categories c ON a.category_id = c.prefix " +
                    "WHERE a.location_id = :location AND a.state IN ('Available', 'Not available', 'Assigned')",
            nativeQuery = true
    )
    Page<AssetEntity> getAllDefault(@Param("location") Integer location, Pageable pageable);

    @Query(value = "SELECT * FROM assets a " +
            "JOIN categories c ON a.category_id = c.prefix " +
            "WHERE a.location_id = :location AND " +
            "(a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword%) AND " +
            "a.category_id IN :category AND " +
            "BINARY a.state IN :state",
            countQuery = "SELECT COUNT(*) FROM assets a " +
                    "JOIN categories c ON a.category_id = c.prefix " +
                    "WHERE a.location_id = :location AND " +
                    "(a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword%) AND " +
                    "a.category_id IN :category AND " +
                    "BINARY a.state IN :state",
            nativeQuery = true
    )
    Page<AssetEntity> getWithFilterAndSearch(
            @Param("location") Integer location,
            @Param("keyword") String keyword,
            @Param("category") List<String> category,
            @Param("state") List<String> state,
            Pageable pageable);

    @Query(value = "SELECT a.*, asm.*, u.*, c.*, lo.* " +
            "FROM assets a " +
            "LEFT JOIN assignments asm ON a.asset_code = asm.asset " +
            "LEFT JOIN users u ON asm.assigned_to = u.username " +
            "INNER JOIN categories c ON a.category_id = c.prefix " +
            "INNER JOIN locations lo ON a.location_id = lo.id " +
            "WHERE a.asset_code = :assetCode ", nativeQuery = true)
    Optional<AssetEntity> getByAssetCode(@Param("assetCode") String assetCode);
}
