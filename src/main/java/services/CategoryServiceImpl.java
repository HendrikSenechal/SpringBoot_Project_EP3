package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Category;
import exception.EntityNotFoundException;
import repository.CategoryRepository;

public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Iterable<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
	}

	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	public Category deleteCategory(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
		categoryRepository.delete(category);
		return category;
	}
}
