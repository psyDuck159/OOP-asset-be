package ptit.oop.assetmanagement.repositories;

import ptit.oop.assetmanagement.entities.AssignmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Integer> {
	@Query(value = "SELECT DISTINCT asm.*, a.*, uee.*, uer.*, c.*, l.* FROM assignments asm " +
			"INNER JOIN assets a ON asm.asset = a.asset_code " +
			"INNER JOIN users uee ON asm.assigned_to = uee.username " +
			"INNER JOIN users uer ON asm.assigned_by = uer.username " +
			"LEFT JOIN categories c ON a.category_id = c.prefix " +
			"LEFT JOIN locations l ON a.location_id = l.id " +
			"LEFT JOIN returning_requests rr ON rr.assignment_id = asm.id " +
 			"WHERE a.location_id = :location " +
			"AND (a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword% OR uee.username LIKE %:keyword%) " +
			"AND asm.state IN :states " +
			"AND asm.returned_date IS NULL " +
			"AND asm.assigned_date LIKE %:assignedDate% ",
			countQuery = "SELECT COUNT(DISTINCT asm.id) FROM assignments asm " +
					"INNER JOIN assets a ON asm.asset = a.asset_code " +
					"INNER JOIN users uee ON asm.assigned_to = uee.username " +
					"INNER JOIN users uer ON asm.assigned_by = uer.username " +
					"LEFT JOIN categories c ON a.category_id = c.prefix " +
					"LEFT JOIN locations l ON a.location_id = l.id " +
					"LEFT JOIN returning_requests rr ON rr.assignment_id = asm.id " +
					"WHERE a.location_id = :location " +
					"AND (a.name LIKE %:keyword% OR a.asset_code LIKE %:keyword% OR uee.username LIKE %:keyword%) " +
					"AND asm.state IN :states " +
					"AND asm.returned_date IS NULL " +
					"AND asm.assigned_date LIKE %:assignedDate% ",
			nativeQuery = true
	)
	Page<AssignmentEntity> getAssignmentsWithFilterAndSearch(
			@Param("location") Integer location,
			@Param("keyword") String keyword,
			@Param("states") List<String> states,
			@Param("assignedDate") String assignedDate,
			Pageable pageable);

	@Query(value = "SELECT asm.*, a.*, uee.*, uer.* FROM assignments asm " +
			"INNER JOIN assets a ON asm.asset = a.asset_code " +
			"INNER JOIN users uee ON asm.assigned_to = uee.username " +
			"INNER JOIN users uer ON asm.assigned_by = uer.username " +
			"WHERE asm.id = :id ", nativeQuery = true)
	Optional<AssignmentEntity> getAssignmentDetails(@Param("id") Integer id);

	@Query(value = "SELECT DISTINCT asm.*, a.*, uee.*, uer.* FROM assignments asm " +
			"INNER JOIN assets a ON asm.asset = a.asset_code " +
			"INNER JOIN users uee ON asm.assigned_to = uee.username " +
			"INNER JOIN users uer ON asm.assigned_by = uer.username " +
			"LEFT JOIN returning_requests rr ON rr.assignment_id = asm.id " +
			"WHERE asm.assigned_to = :assigneeUsername " +
			"AND asm.state <> 'Declined' " +
			"AND asm.returned_date IS NULL " +
			"AND asm.assigned_date <= CURRENT_DATE() ",
			countQuery = "SELECT COUNT(DISTINCT asm.id) FROM assignments asm " +
					"INNER JOIN assets a ON asm.asset = a.asset_code " +
					"INNER JOIN users uee ON asm.assigned_to = uee.username " +
					"INNER JOIN users uer ON asm.assigned_by = uer.username " +
					"LEFT JOIN returning_requests rr ON rr.assignment_id = asm.id " +
					"WHERE asm.assigned_to = :assigneeUsername " +
					"AND asm.state <> 'Declined' " +
					"AND asm.returned_date IS NULL " +
					"AND asm.assigned_date <= CURRENT_DATE() ",
			nativeQuery = true)
	Page<AssignmentEntity> getMyAssignments(@Param("assigneeUsername") String assigneeUsername, Pageable pageable);
}
