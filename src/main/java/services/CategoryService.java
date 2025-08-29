package services;

import entity.Category;

public interface CategoryService {
	Iterable<Category> getAllCategories();

	Category getCategoryById(Long id);

	Category createCategory(Category category);

	Category deleteCategory(Long id);
}
