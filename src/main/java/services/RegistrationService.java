package services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;

public interface RegistrationService {
	Page<Registration> getRegistrations(Long festivalId, Pageable pageable);

	Registration getRegistrationById(Long festivalId, Long UserId);

	Double getAverageRatingForFestival(Long festivalId);

	List<Registration> getTop10Reviews(Long festivalId);
}
