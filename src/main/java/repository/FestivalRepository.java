package repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entity.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long>, JpaSpecificationExecutor<Festival> {

	@Query("""
			  SELECT COUNT(f) FROM Festival f
			  WHERE f.name = :name
			    AND NOT (:end < f.start OR :start > f.end)
			""")
	long countNameOverlap(@Param("name") String name, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			  SELECT COUNT(f) FROM Festival f
			  WHERE f.category.id = :categoryId
			    AND NOT (:end < f.start OR :start > f.end)
			""")
	long countCategoryOverlap(@Param("categoryId") Long categoryId, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query("""
			    select count(f) > 0
			    from Festival f
			    where lower(f.name) = lower(:name)
			      and f.start <= :endDate
			      and f.end   >= :startDate
			""")
	boolean existsOverlapByName(@Param("name") String name, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	// For UPDATE (exclude the current entity)
	@Query("""
			    select count(f) > 0
			    from Festival f
			    where lower(f.name) = lower(:name)
			      and f.id <> :id
			      and f.start <= :endDate
			      and f.end   >= :startDate
			""")
	boolean existsOverlapByNameExcludingId(@Param("name") String name, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("id") Long excludeId);
}
