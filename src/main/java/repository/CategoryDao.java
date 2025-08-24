package repository;

import java.util.List;

import entity.Category;

public interface CategoryDao extends GenericDao<Category> {
	List<Category> getAllCategories();

	void updateCategory(Category cagtegory);

	void addCategory(Category category);
}
