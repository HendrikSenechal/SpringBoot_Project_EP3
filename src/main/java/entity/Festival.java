package entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
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
 * Represents a festival event with details such as name, description, schedule,
 * pricing, and associations to address, category, vendors, and registrations.
 *
 * <p>
 * This entity maps to the "festival" table in the database and manages
 * relationships with {@link Address}, {@link Category}, {@link Vendor}, and
 * {@link Registration}.
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Festival.findAll", query = "SELECT f from Festival f"),
		@NamedQuery(name = "Festival.findById", query = "SELECT f FROM Festival f WHERE f.id = :id"), })

@Table(name = "festival")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id", "registrations", "vendors" })
@ToString(exclude = { "id", "registrations" })
public class Festival implements Serializable, BaseEntity {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier for the festival.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Getter(AccessLevel.NONE)
	private Long id;

	/**
	 * Name of the festival.
	 */
	private String name;

	/**
	 * Description of the festival.
	 */
	private String description;

	/**
	 * Start date and time of the festival.
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime start;

	/**
	 * End date and time of the festival.
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime end;

	/**
	 * Price for attending the festival.
	 */
	private int price;

	/**
	 * Total number of tickets or attendees allowed.
	 */
	private int amount;

	/**
	 * Address where the festival takes place.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;

	/**
	 * Category of the festival.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	/**
	 * Registrations associated with this festival.
	 */
	@OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Registration> registrations = new HashSet<>();

	/**
	 * Vendors associated with this festival.
	 */
	@ManyToMany
	@JoinTable(name = "FESTIVAL_VENDOR", joinColumns = @JoinColumn(name = "FESTIVAL_ID"), inverseJoinColumns = @JoinColumn(name = "VENDOR_ID"))
	private Set<Vendor> vendors = new HashSet<>();

	/**
	 * Constructs a Festival with the specified name, description, price, and
	 * amount. Does not include relationships or scheduling details. Used for
	 * database population in populateDB.
	 *
	 * @param name        the name of the festival
	 * @param description the description of the festival
	 * @param price       the price of the festival
	 * @param amount      the maximum number of attendees
	 */
	public Festival(String name, String description, int price, int amount) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.amount = amount;
	}
}
