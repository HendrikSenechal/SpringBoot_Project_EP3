package entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import utility.PasswordUtil;

/**
 * Represents a system user who can register for festivals.
 * <p>
 * Each user has a role (e.g., ADMIN, GUEST), login credentials, and is
 * associated with one or more festival registrations.
 * </p>
 */
@Entity
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u from User u"),
		@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"), })
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id", "registrations" })
@ToString(exclude = { "id", "registrations" })
public class User implements Serializable, BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier for the user (primary key).
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Getter(AccessLevel.NONE)
	private Long id;

	/**
	 * User's last name.
	 */
	private String name;

	/**
	 * User's first name.
	 */
	@Column(name = "FIRST_NAME")
	private String firstName;

	/**
	 * User's email address (used for login/contact).
	 */
	private String email;

	/**
	 * User's phone number.
	 */
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	/**
	 * User's role within the system (e.g., ADMIN, ORGANIZER, USER).
	 */
	private Role role;

	/**
	 * Salt used for password hashing. Automatically generated.
	 */
	@Column(nullable = false)
	private byte[] salt = PasswordUtil.generateSalt();

	/**
	 * Transient field holding the plain password (not persisted).
	 */
	@Transient
	private String plainPassword;

	/**
	 * Hashed password stored securely in the database.
	 */
	private String password;

	/**
	 * Registrations made by the user for various festivals.
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Registration> registrations = new HashSet<>();

	/**
	 * Constructs a new User with provided attributes and hashes the password. Used
	 * for database population in populateDB.
	 *
	 * @param name        the user's last name
	 * @param firstName   the user's first name
	 * @param email       the user's email
	 * @param phoneNumber the user's phone number
	 * @param role        the user's role
	 * @param password    the user's plain password (will be hashed)
	 */
	public User(String name, String firstName, String email, String phoneNumber, Role role, String password) {
		this.name = name;
		this.firstName = firstName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
		setPassword(password);
	}

	/**
	 * Hashes and stores the password using the generated salt.
	 *
	 * @param password the plain password to hash
	 */
	private void setPassword(String password) {
		this.plainPassword = password;
		this.password = PasswordUtil.hashPassword(password, salt);
	}
}
