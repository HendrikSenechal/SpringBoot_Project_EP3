package repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

/**
 * JPA implementation of {@link UserDao} for managing {@link User} entities.
 * Provides CRUD operations and additional user-specific queries.
 */
public class UserDaoJpa extends GenericDaoJpa<User> implements UserDao {

	private static final Logger log = LoggerFactory.getLogger(UserDaoJpa.class);

	/**
	 * Constructs a {@code UserDaoJpa} for the {@code User} entity class.
	 */
	public UserDaoJpa() {
		super(User.class);
		log.info("UserDaoJpa initialized");
	}

	/**
	 * Retrieves all users from the database.
	 *
	 * @return a list of all {@link User} entities
	 * @throws EntityNotFoundException if no users are found
	 */
	public List<User> getAllUsers() {
		log.info("Fetching all users");
		try {
			List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();
			log.info("Fetched {} users", users.size());
			return users;
		} catch (NoResultException ex) {
			log.warn("No users found", ex);
			throw new EntityNotFoundException("No users found");
		}
	}

	/**
	 * Updates the specified {@link User} entity in the database.
	 *
	 * @param user the {@code User} entity to update
	 */
	@Override
	public void updateUser(User user) {
		log.info("Updating user with ID {}", user.getId());
		runInTransaction(() -> {
			super.update(user);
			log.info("User with ID {} updated", user.getId());
		});
	}

	/**
	 * Adds a new {@link User} entity to the database.
	 *
	 * @param user the {@code User} entity to add
	 */
	@Override
	public void addUser(User user) {
		log.info("Adding new user");
		runInTransaction(() -> {
			super.insert(user);
			log.info("New user added");
		});
	}

	/**
	 * Finds a {@link User} entity by its email address.
	 *
	 * @param email the email address to search for
	 * @return the matching {@code User} entity or {@code null} if no match is found
	 */
	public User findByEmail(String email) {
		log.info("Searching for user by email: {}", email);
		try {
			User user = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
					.getSingleResult();
			log.info("User found with email: {}", email);
			return user;
		} catch (NoResultException e) {
			log.info("No user found with email: {}", email);
			return null;
		}
	}
}
