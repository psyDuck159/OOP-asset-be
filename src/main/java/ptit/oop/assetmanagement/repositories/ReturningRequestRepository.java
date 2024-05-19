package ptit.oop.assetmanagement.repositories;

import ptit.oop.assetmanagement.entities.ReturningRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturningRequestRepository extends JpaRepository<ReturningRequestEntity, Integer> {

	@Query(value = "SELECT DISTINCT rr.*, asm.*, a.*, uab.*, urb.*, l.* FROM returning_requests rr " +
			"INNER JOIN assignments asm ON rr.assignment_id = asm.id " +
			"INNER JOIN users urb ON rr.requested_by = urb.username " +
			"LEFT JOIN users uab ON rr.accepted_by = uab.username " +
			"LEFT JOIN assets a ON asm.asset = a.asset_code " +
			"LEFT JOIN locations l ON a.location_id = l.id " +
			"WHERE a.location_id = :location " +
			"AND (a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword% OR urb.username LIKE %:keyword%) " +
			"AND rr.state IN :states " +
			"AND IFNULL(asm.returned_date, '') LIKE %:date% ",
			countQuery = "SELECT COUNT(DISTINCT rr.id) FROM returning_requests rr " +
					"INNER JOIN assignments asm ON rr.assignment_id = asm.id " +
					"INNER JOIN users urb ON rr.requested_by = urb.username " +
					"LEFT JOIN users uab ON rr.accepted_by = uab.username " +
					"LEFT JOIN assets a ON asm.asset = a.asset_code " +
					"LEFT JOIN locations l ON a.location_id = l.id " +
					"WHERE a.location_id = :location " +
					"AND (a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword% OR urb.username LIKE %:keyword%) " +
					"AND rr.state IN :states " +
					"AND IFNULL(asm.returned_date, '') LIKE %:date% "
			,
			nativeQuery = true)
	Page<ReturningRequestEntity> getAll(
			@Param("location") Integer location,
			@Param("keyword") String keyword,
			@Param("states") List<String> states,
			@Param("date") String date,
			Pageable pageable);
}
