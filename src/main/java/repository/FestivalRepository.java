package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
