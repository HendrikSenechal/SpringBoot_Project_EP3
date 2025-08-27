package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a registration of a {@link MyUser} for a {@link Festival},
 * including rating, comment, and timestamp.
 * <p>
 * This entity uses a composite primary key defined in {@link UserFestivalKey}.
 * </p>
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Registration.findAll", query = "SELECT r from Registration r"), })
@Table(name = "registration")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
	@Nullable // signals "may be null" to IDEs & Spring
	@Column(name = "rating", nullable = true) // DB column allows NULL
	private Integer rating;

	/**
	 * Amount of tickets ordered by the user for the festival.
	 */
	private int tickets;

	/**
	 * Optional comment left by the user. Stored as large text.
	 */
	@Lob
	@Column(columnDefinition = "TEXT")
	private String comment;

	/**
	 * Optional comment left by the user. Stored as large text.
	 */
	@Lob
	@Column(columnDefinition = "TEXT")
	private String detailDescription;

	/**
	 * Timestamp of the registration.
	 */
	private LocalDateTime date;

	/**
	 * The user associated with this registration.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("userId")
	@JoinColumn(name = "USER_ID")
	private MyUser myUser;

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
	 * @param rating   the ticket amount ordered by the user
	 * @param comment  the comment made by the user
	 * @param date     the time of registration
	 * @param myUser   the registered user
	 * @param festival the registered festival
	 */
	public Registration(Integer rating, int tickets, String comment, String detailDescription, LocalDateTime date,
			MyUser myUser, Festival festival) {
		this.rating = rating;
		this.tickets = tickets;
		this.comment = comment;
		this.detailDescription = detailDescription;
		this.date = date;
		this.myUser = myUser;
		this.festival = festival;
	}
}
