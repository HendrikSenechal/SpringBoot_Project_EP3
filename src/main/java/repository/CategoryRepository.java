package repository;

import org.springframework.data.repository.CrudRepository;

import entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

}
