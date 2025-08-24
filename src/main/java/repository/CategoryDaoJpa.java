package repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.Category;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

public class CategoryDaoJpa extends GenericDaoJpa<Category> implements CategoryDao {

	private static final Logger log = LoggerFactory.getLogger(CategoryDaoJpa.class);

	public CategoryDaoJpa() {
		super(Category.class);
		log.info("CategoryDaoJpa initialized");
	}

	public List<Category> getAllCategories() {
		log.info("Fetching all categories");
		try {
			List<Category> categories = em.createNamedQuery("Category.findAll", Category.class).getResultList();
			log.info("Fetched {} categories", categories.size());
			return categories;
		} catch (NoResultException ex) {
			log.warn("No categories found", ex);
			throw new EntityNotFoundException("No categories found");
		}
	}

	@Override
	public void updateCategory(Category category) {
		log.info("Updating category with ID {}", category.getId());
		runInTransaction(() -> {
			super.update(category);
			log.info("Category with ID {} updated", category.getId());
		});
	}

	@Override
	public void addCategory(Category category) {
		log.info("Adding new category");
		runInTransaction(() -> {
			super.insert(category);
			log.info("New category added");
		});
	}
}
