package repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationRepository extends JpaRepository<Registration, UserFestivalKey> {
	Page<Registration> findByIdFestivalId(Long festivalId, Pageable pageable);

	@Query("SELECT AVG(r.rating) FROM Registration r WHERE r.id.festivalId = :festivalId")
	Double findAverageRatingByFestivalId(@Param("festivalId") Long festivalId);
}
