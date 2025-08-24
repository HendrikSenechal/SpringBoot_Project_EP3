package repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.Registration;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

/**
 * JPA implementation of {@link RegistrationDao} for managing
 * {@link Registration} entities. Provides methods to perform CRUD operations on
 * Registration objects.
 */
public class RegistrationDaoJpa extends GenericDaoJpa<Registration> implements RegistrationDao {

	private static final Logger log = LoggerFactory.getLogger(RegistrationDaoJpa.class);

	/**
	 * Constructs a {@code RegistrationDaoJpa} for the {@code Registration} entity
	 * class.
	 */
	public RegistrationDaoJpa() {
		super(Registration.class);
		log.info("RegistrationDaoJpa initialized");
	}

	/**
	 * Retrieves all registrations from the database.
	 *
	 * @return a list of all {@link Registration} entities
	 * @throws EntityNotFoundException if no registrations are found
	 */
	public List<Registration> getAllRegistrations() {
		log.info("Fetching all registrations");
		try {
			List<Registration> registrations = em.createNamedQuery("Registration.findAll", Registration.class)
					.getResultList();
			log.info("Fetched {} registrations", registrations.size());
			return registrations;
		} catch (NoResultException ex) {
			log.warn("No registrations found", ex);
			throw new EntityNotFoundException("No registrations found");
		}
	}

	/**
	 * Updates the specified {@link Registration} in the database.
	 *
	 * @param registration the {@code Registration} entity to update
	 */
	@Override
	public void updateRegistration(Registration registration) {
		log.info("Updating registration with ID {}", registration.getId());
		runInTransaction(() -> {
			super.update(registration);
			log.info("Registration with ID {} updated", registration.getId());
		});
	}

	/**
	 * Adds a new {@link Registration} to the database.
	 *
	 * @param registration the {@code Registration} entity to add
	 */
	@Override
	public void addRegistration(Registration registration) {
		log.info("Adding new registration");
		runInTransaction(() -> {
			super.insert(registration);
			log.info("New registration added");
		});
	}
}
