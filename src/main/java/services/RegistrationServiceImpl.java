package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;
import entity.UserFestivalKey;
import repository.RegistrationRepository;

public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegistrationRepository registrationRepository;

	@Override
	public Page<Registration> getRegistrations(Long festivalId, Pageable pageable) {
		return registrationRepository.findByIdFestivalId(festivalId, pageable);
	}

	@Override
	public Registration getRegistrationById(UserFestivalKey key) {
		return registrationRepository.findById(key).orElseThrow(() -> new RuntimeException("Registration not found"));
	}

	@Override
	public Double getAverageRatingForFestival(Long festivalId) {
		Double avg = Math.round(registrationRepository.findAverageRatingByFestivalId(festivalId) * 10.0) / 10.0;
		return avg != null ? avg : 0.0;
	}
}
