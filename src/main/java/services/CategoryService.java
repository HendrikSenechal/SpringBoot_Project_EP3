package services;

import entity.Category;

public interface CategoryService {
	Iterable<Category> getAllCategories();

	Category getCategoryById(Long id);
}
