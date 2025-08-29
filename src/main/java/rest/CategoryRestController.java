package rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Category;
import services.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {
	@Autowired
	private CategoryService categoryService;

	@PostMapping("/new")
	public Category createCategory(@RequestBody Category category) {
		return categoryService.createCategory(category);
	}

	@GetMapping("/{id}")
	public Category getCategory(@PathVariable Long id) {
		return categoryService.getCategoryById(id);
	}

	@GetMapping("")
	public List<Category> getAllCategories() {
		return (List<Category>) categoryService.getAllCategories();
	}

	@DeleteMapping("/{id}")
	public Category deleteCategory(@PathVariable Long id) {
		return categoryService.deleteCategory(id);
	}
}
