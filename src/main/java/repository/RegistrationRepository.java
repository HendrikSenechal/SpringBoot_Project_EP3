package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationRepository extends JpaRepository<Registration, UserFestivalKey> {
	@EntityGraph(attributePaths = "myUser")
	@Query("""
			   SELECT r FROM Registration r
			   WHERE r.festival.id = :festivalId
			     AND r.myUser.id <> :userId
			""")
	Page<Registration> findByFestivalIdExcludingUser(@Param("festivalId") Long festivalId, @Param("userId") Long userId,
			Pageable pageable);

	@Query("SELECT AVG(r.rating) FROM Registration r WHERE r.id.festivalId = :festivalId")
	Double findAverageRatingByFestivalId(@Param("festivalId") Long festivalId);

	List<Registration> findTop10ByIdFestivalIdOrderByRatingDesc(Long festivalId);

	Optional<Registration> findByFestivalIdAndMyUserId(Long festivalId, Long userId);
}
