package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a registration of a {@link User} for a {@link Festival}, including
 * rating, comment, and timestamp.
 * <p>
 * This entity uses a composite primary key defined in {@link UserFestivalKey}.
 * </p>
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Registration.findAll", query = "SELECT r from Registration r"), })
@Table(name = "registration")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id" })
@ToString(exclude = { "id" })
public class Registration implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Composite primary key linking a user and a festival.
	 */
	@EmbeddedId
	private UserFestivalKey id = new UserFestivalKey();

	/**
	 * Rating given by the user for the festival.
	 */
	private int rating;

	/**
	 * Optional comment left by the user. Stored as large text.
	 */
	@Lob
	@Column(columnDefinition = "TEXT")
	private String comment;

	/**
	 * Timestamp of the registration.
	 */
	private LocalDateTime date;

	/**
	 * The user associated with this registration.
	 */
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "USER_ID")
	private User user;

	/**
	 * The festival associated with this registration.
	 */
	@ManyToOne
	@MapsId("festivalId")
	@JoinColumn(name = "FESTIVAL_ID")
	private Festival festival;

	/**
	 * Creates a registration entry. Used for database population in populateDB.
	 *
	 * @param rating   the rating given by the user
	 * @param comment  the comment made by the user
	 * @param date     the time of registration
	 * @param user     the registered user
	 * @param festival the registered festival
	 */
	public Registration(int rating, String comment, LocalDateTime date, User user, Festival festival) {
		this.rating = rating;
		this.comment = comment;
		this.date = date;
		this.user = user;
		this.festival = festival;
	}
}
