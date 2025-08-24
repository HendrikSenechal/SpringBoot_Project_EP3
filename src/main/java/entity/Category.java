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
 * Represents a category for festivals and vendors.
 *
 * <p>
 * This entity maps to the "category" table in the database and supports
 * bidirectional relationships with {@link Festival} and {@link Vendor}.
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Category.findAll", query = "SELECT a from Category a"), })
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "id", "festivalsOfCategory", "vendorsOfCategory" })
@ToString(exclude = { "id", "festivalsOfCategory", "vendorsOfCategory" })
public class Category implements Serializable, BaseEntity {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key identifier for the category.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Getter(AccessLevel.NONE)
	private Long id;

	/**
	 * Name of the category.
	 */
	private String name;

	/**
	 * Description of the category.
	 */
	private String description;

	/**
	 * List of festivals associated with this category.
	 */
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Festival> festivalsOfCategory = new ArrayList<>();

	/**
	 * List of vendors associated with this category.
	 */
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vendor> vendorsOfCategory = new ArrayList<>();

	/**
	 * Constructs a new Category with the given name and description. Used for
	 * database population in populateDB.
	 *
	 * @param name        the name of the category
	 * @param description the description of the category
	 */
	public Category(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
