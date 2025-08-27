package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationRepository extends JpaRepository<Registration, UserFestivalKey> {
	@EntityGraph(attributePaths = "myUser")
	@Query("""
			SELECT r
			FROM Registration r
			WHERE r.festival.id = :festivalId
			  AND r.myUser.id <> :userId
			  AND r.rating IS NOT NULL
			""")
	Page<Registration> findByFestivalIdExcludingUser(@Param("festivalId") Long festivalId, @Param("userId") Long userId,
			Pageable pageable);

	@Query("""
			SELECT r
			FROM Registration r
			WHERE r.id.festivalId = :festivalId
			  AND r.rating IS NOT NULL
			ORDER BY r.rating DESC
			""")
	List<Registration> findTop10ByIdFestivalIdOrderByRatingDesc(@Param("festivalId") Long festivalId);

	Optional<Registration> findByFestivalIdAndMyUserId(@Param("festivalId") Long festivalId,
			@Param("userId") Long userId);

	// AGGREGATIONS
	@Query("""
			SELECT COALESCE(AVG(r.rating), 0)
			FROM Registration r
			WHERE r.festival.id = :festivalId
			""")
	Double findAverageRatingByFestivalId(@Param("festivalId") Long festivalId);

	@Query("""
			SELECT COALESCE(SUM(r.tickets), 0)
			FROM Registration r
			WHERE r.id.festivalId = :festivalId
			""")
	int sumTicketsForFestival(@Param("festivalId") Long festivalId);

	@Query("""
			SELECT COALESCE(SUM(r.tickets), 0)
			FROM Registration r
			WHERE r.festival.id = :festivalId
			  AND r.myUser.id = :userId
			""")
	long sumTicketsForFestivalAndCurrentUser(@Param("festivalId") Long festivalId, @Param("userId") Long userId);

	// MUTATIONS

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			UPDATE Registration r
			SET r.tickets = :tickets
			WHERE r.id.userId = :userId
			  AND r.id.festivalId = :festivalId
			""")
	int updateTickets(@Param("festivalId") Long festivalId, @Param("userId") Long userId,
			@Param("tickets") Integer tickets);

	@Modifying
	@Transactional
	@Query("""
			DELETE FROM Registration r
			WHERE r.id.festivalId = :festivalId
			  AND r.id.userId = :userId
			""")
	void deleteByFestivalIdAndUserId(@Param("festivalId") Long festivalId, @Param("userId") Long userId);

}
