package entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Composite primary key for the {@link Registration} entity.
 * <p>
 * This class represents a combination of a user and a festival, uniquely
 * identifying a registration.
 * </p>
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString(of = { "userId", "festivalId" })
public class UserFestivalKey implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID of the user associated with the registration.
	 */
	@Column(name = "user_id")
	private Long userId;

	/**
	 * ID of the festival associated with the registration.
	 */
	@Column(name = "festival_id")
	private Long festivalId;

	/**
	 * Constructs a new {@code UserFestivalKey} with the given user and festival
	 * IDs.
	 *
	 * @param userId     the user's ID
	 * @param festivalId the festival's ID
	 */
	public UserFestivalKey(Long userId, Long festivalId) {
		this.userId = userId;
		this.festivalId = festivalId;
	}

	/**
	 * Determines equality based on both userId and festivalId.
	 *
	 * @param o the other object
	 * @return true if both IDs are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UserFestivalKey))
			return false;
		UserFestivalKey that = (UserFestivalKey) o;
		return Objects.equals(userId, that.userId) && Objects.equals(festivalId, that.festivalId);
	}

	/**
	 * Computes hash code using userId and festivalId.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(userId, festivalId);
	}
}
