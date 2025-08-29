package entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import validation.NoDuplicateVendors;
import validation.WithinFestivalPeriod;

/**
 * Represents a festival event with details such as name, description, schedule,
 * pricing, and associations to address, category, vendors, and registrations.
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
@WithinFestivalPeriod(startField = "start", endField = "end", message = "Start/eind vallen buiten de festivalperiode (01-06-2025 t/m 31-08-2025) of eind is vóór start.")
public class Festival implements Serializable, BaseEntity {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Naam is verplicht.")
	@Pattern(regexp = "^[A-Za-z]{3}.*$", message = "Naam moet beginnen met drie letters.")
	private String name;

	private String description;

	@NotNull(message = "Startdatum/-tijd is verplicht.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime start;

	@NotNull(message = "Einddatum/-tijd is verplicht.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime end;

	@Min(value = 2, message = "FestivalCode1 moet strikt positief zijn en minimaal 2.")
	private int festivalCode1;

	@Min(value = 3, message = "FestivalCode2 moet minimaal 3 zijn.")
	private int festivalCode2;

	@NotNull(message = "Prijs is verplicht.")
	@DecimalMin(value = "10.50", inclusive = true, message = "Prijs moet ≥ 10,50 zijn.")
	@DecimalMax(value = "40.00", inclusive = false, message = "Prijs moet < 40,00 zijn.")
	@Digits(integer = 4, fraction = 2, message = "Prijs mag maximaal 2 decimalen hebben.")
	@Column(precision = 8, scale = 2)
	private BigDecimal price;

	@Min(value = 50, message = "Aantal moet minimaal 50 zijn.")
	@Max(value = 250, message = "Aantal mag maximaal 250 zijn.")
	private int amount;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	// @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval =
	// true)
	@JsonIgnore
	@OneToMany(mappedBy = "festival", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, // not REMOVE
			orphanRemoval = false)
	private Set<Registration> registrations = new HashSet<>();

	@NoDuplicateVendors
	@ManyToMany
	@JsonIgnore
	@JoinTable(name = "FESTIVAL_VENDOR", joinColumns = @JoinColumn(name = "FESTIVAL_ID"), inverseJoinColumns = @JoinColumn(name = "VENDOR_ID"))
	@Valid
	private Set<Vendor> vendors = new HashSet<>();

	public Festival(String name, String description, int festivalCode1, int festivalCode2, BigDecimal price,
			int amount) {
		this.name = name;
		this.description = description;
		this.festivalCode1 = festivalCode1;
		this.festivalCode2 = festivalCode2;
		this.price = price;
		this.amount = amount;
	}
}
