package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
