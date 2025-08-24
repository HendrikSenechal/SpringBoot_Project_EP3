package repository;

import org.springframework.data.repository.CrudRepository;

import entity.Festival;

public interface FestivalRepository extends CrudRepository<Festival, Long> {

}
