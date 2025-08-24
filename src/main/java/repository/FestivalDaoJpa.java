package repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.Festival;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

/**
 * JPA implementation of {@link FestivalDao} for managing {@link Festival}
 * entities. Provides methods to perform CRUD operations on Festival objects.
 */
public class FestivalDaoJpa extends GenericDaoJpa<Festival> implements FestivalDao {

	private static final Logger log = LoggerFactory.getLogger(FestivalDaoJpa.class);

	/**
	 * Constructs a {@code FestivalDaoJpa} for the {@code Festival} entity class.
	 */
	public FestivalDaoJpa() {
		super(Festival.class);
		log.info("FestivalDaoJpa initialized");
	}

	/**
	 * Retrieves all festivals from the database.
	 *
	 * @return a list of all {@link Festival} entities
	 * @throws EntityNotFoundException if no festivals are found
	 */
	public List<Festival> getAllFestivals() {
		log.info("Fetching all festivals");
		try {
			List<Festival> festivals = em.createNamedQuery("Festival.findAll", Festival.class).getResultList();
			log.info("Fetched {} festivals", festivals.size());
			return festivals;
		} catch (NoResultException ex) {
			log.warn("No festivals found", ex);
			throw new EntityNotFoundException("No festivals found");
		}
	}

	/**
	 * Retrieves all festivals from the database.
	 * 
	 * @param Long the {@code id of festival} entity to update
	 *
	 * @return a instance of {@link Festival} entity with said id
	 * @throws EntityNotFoundException if no festival is found
	 */
	@Override
	public Festival getFestivalById(Long id) {
		log.info("Fetching festival with id {}", id);
		try {
			Festival festival = em.createNamedQuery("Festival.findById", Festival.class).setParameter("id", id)
					.getSingleResult();
			log.info("Fetched {} festival", festival.getId());
			return festival;
		} catch (NoResultException ex) {
			log.warn("No festival found");
			throw new EntityNotFoundException("No festival found");
		}
	}

	/**
	 * Updates the specified {@link Festival} in the database.
	 *
	 * @param festival the {@code Festival} entity to update
	 */
	@Override
	public void updateFestival(Festival festival) {
		log.info("Updating festival with ID {}", festival.getId());
		runInTransaction(() -> {
			super.update(festival);
			log.info("Festival with ID {} updated", festival.getId());
		});
	}

	/**
	 * Adds a new {@link Festival} to the database.
	 *
	 * @param festival the {@code Festival} entity to add
	 */
	@Override
	public void addFestival(Festival festival) {
		log.info("Adding new festival");
		runInTransaction(() -> {
			super.insert(festival);
			log.info("New festival added");
		});
	}

	@Override
	public List<Festival> getFestivalsPaginated(int page, int pageSize) {
		log.info("Fetching festivals - page {}, pageSize {}", page, pageSize);
		return em.createQuery("SELECT f FROM Festival f ORDER BY f.start", Festival.class)
				.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
	}

	@Override
	public long countFestivals() {
		return em.createQuery("SELECT COUNT(f) FROM Festival f", Long.class).getSingleResult();
	}
}
