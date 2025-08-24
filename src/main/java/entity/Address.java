package entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an address entity that can be associated with multiple festivals
 * and vendors.
 * 
 * <p>
 * This class is annotated as a JPA entity and maps to the "address" table in
 * the database.
 * </p>
 * 
 * <p>
 * It includes basic address fields such as country, place, street, and number,
 * and manages bidirectional relationships with {@link Festival} and
 * {@link Vendor} entities.
 * </p>
 * 
 * <p>
 * The class implements {@link Serializable} to allow instances to be serialized
 * and transferred or persisted.
 * </p>
 * 
 * @author
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Address.findAll", query = "SELECT a from Address a"),
		@NamedQuery(name = "Address.findById", query = "SELECT a FROM Address a WHERE a.id = :id"), })
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id", "festivalsOfAddress", "vendorsOfAddress" })
@ToString(exclude = { "id", "festivalsOfAddress", "vendorsOfAddress" })
public class Address implements Serializable, BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/** Primary key ID for the address (auto-generated). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Getter(AccessLevel.NONE)
	private Long id;

	/** Name of the address (e.g., venue or location name). */
	private String name;

	/** Country where the address is located. */
	private String country;

	/** City or town name. */
	private String place;

	/** Postal code associated with the address. */
	private int postcode;

	/** Street name. */
	private String street;

	/** House or building number. */
	private int number;

	/** Optional addition to the address number (e.g., unit or apartment). */
	private String addition;

	/**
	 * List of festivals associated with this address. Mapped by the "address" field
	 * in the {@link Festival} entity.
	 */
	@OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Festival> festivalsOfAddress = new ArrayList<>();

	/**
	 * List of vendors associated with this address. Mapped by the "address" field
	 * in the {@link Vendor} entity.
	 */
	@OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vendor> vendorsOfAddress = new ArrayList<>();

	/**
	 * Constructs an {@code Address} with all core address fields. Used for database
	 * population in populateDB.
	 * 
	 * @param name     name of the address (e.g., venue)
	 * @param country  country name
	 * @param place    city or town
	 * @param postcode postal code
	 * @param street   street name
	 * @param number   building number
	 * @param addition additional address detail (e.g., unit or apartment)
	 */
	public Address(String name, String country, String place, int postcode, String street, int number,
			String addition) {
		this.name = name;
		this.country = country;
		this.place = place;
		this.postcode = postcode;
		this.street = street;
		this.number = number;
		this.addition = addition;
	}
}
