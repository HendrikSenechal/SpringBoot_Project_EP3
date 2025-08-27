package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import entity.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long>, JpaSpecificationExecutor<Festival> {
}
