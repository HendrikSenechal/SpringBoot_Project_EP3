package entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
 * Represents a vendor participating in one or more festivals.
 * <p>
 * Vendors are associated with a category and an address and may be linked to
 * multiple festivals via a many-to-many relationship.
 * </p>
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Vendor.findAll", query = "SELECT v FROM Vendor v"),
		@NamedQuery(name = "Vendor.findById", query = "SELECT v FROM Vendor v WHERE v.id = :id"),
		@NamedQuery(name = "Vendor.findByIds", query = "SELECT v FROM Vendor v WHERE v.id IN :ids") })
@Table(name = "vendor")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id", "festivals" })
@ToString(exclude = { "id", "festivals" })
public class Vendor implements Serializable, BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/** Unique identifier for the vendor. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Getter(AccessLevel.NONE)
	private Long id;

	/** Name of the vendor. */
	private String name;

	/** Description of the vendor's offerings or services. */
	private String description;

	/** Contact phone number of the vendor. */
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	/** Contact email address. */
	private String email;

	/** Website URL of the vendor. */
	private String website;

	/** URL of the vendor's logo image. */
	@Column(name = "LOGO_URL")
	private String logoUrl;

	/** Rating score of the vendor. */
	@Column(name = "VENDOR_RATING")
	private int vendorRating;

	/** The category to which this vendor belongs. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	/** The address associated with this vendor. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;

	/** Festivals that this vendor is participating in. */
	@JsonIgnore
	@ManyToMany(mappedBy = "vendors")
	private Set<Festival> festivals = new HashSet<>();

	/**
	 * Constructs a vendor with the provided basic details.
	 *
	 * @param name         the vendor's name
	 * @param description  description of the vendor
	 * @param phoneNumber  vendor's phone number
	 * @param email        vendor's email address
	 * @param website      vendor's website
	 * @param logoUrl      URL to the vendor's logo
	 * @param vendorRating rating assigned to the vendor
	 */
	public Vendor(String name, String description, String phoneNumber, String email, String website, String logoUrl,
			int vendorRating) {
		this.name = name;
		this.description = description;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.website = website;
		this.logoUrl = logoUrl;
		this.vendorRating = vendorRating;
	}
}
