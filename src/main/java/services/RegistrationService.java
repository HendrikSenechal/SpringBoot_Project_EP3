package services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationService {
	Page<Registration> getRegistrations(Long festivalId, Pageable pageable);

	Registration getRegistrationById(UserFestivalKey key);

	Double getAverageRatingForFestival(Long festivalId);
}
