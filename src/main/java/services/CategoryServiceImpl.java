package services;

import org.springframework.beans.factory.annotation.Autowired;

import entity.Category;
import repository.CategoryRepository;

public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Iterable<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
}
