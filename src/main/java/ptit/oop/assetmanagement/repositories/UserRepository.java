package ptit.oop.assetmanagement.repositories;

import ptit.oop.assetmanagement.entities.LocationEntity;
import ptit.oop.assetmanagement.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findFirstByUsernameStartsWithOrderByUsernameDesc(String username);
    Optional<UserEntity> findFirstByOrderByStaffCodeDesc();
    Long countByUsernameStartsWith(String username);

    @Query(value = "select * from users where location_id = :location and enable = 1 and (LOWER(staff_code) like %:keyword% or LOWER(CONCAT(first_name, \" \", last_name)) like %:keyword% or username like %:keyword%) and type like %:type%",
    countQuery = "select count(*) from users where location_id = :location and enable = 1 and (LOWER(staff_code) like %:keyword% or LOWER(CONCAT(first_name, \" \", last_name)) like %:keyword% or username like %:keyword%) and type like %:type%",
    nativeQuery = true)
    Page<UserEntity> getAll(@Param("location") Integer location, @Param("keyword") String keyword, @Param("type") String type, Pageable pageable);

    Optional<UserEntity> findByUsernameAndLocation(String username, LocationEntity location);
}
