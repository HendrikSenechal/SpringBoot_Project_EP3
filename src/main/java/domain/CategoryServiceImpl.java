package domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import entity.Category;
import repository.CategoryDaoJpa;

public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryDaoJpa categoryRepository;

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.getAllCategories();
	}
}
