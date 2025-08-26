package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import entity.Registration;
import repository.RegistrationRepository;
import security.CustomUserDetails;

public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegistrationRepository registrationRepository;

	@Override
	public Page<Registration> getRegistrations(Long festivalId, Pageable pageable) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return registrationRepository.findByFestivalIdExcludingUser(festivalId, userDetails.getId(), pageable);
	}

	@Override
	public Registration getRegistrationById(Long festivalId, Long UserId) {
		return registrationRepository.findByFestivalIdAndMyUserId(festivalId, UserId)
				.orElseThrow(() -> new RuntimeException("Registration not found"));
	}

	@Override
	public Double getAverageRatingForFestival(Long festivalId) {
		Double avg = Math.round(registrationRepository.findAverageRatingByFestivalId(festivalId) * 10.0) / 10.0;
		return avg != null ? avg : 0.0;
	}

	@Override
	public List<Registration> getTop10Reviews(Long festivalId) {
		return registrationRepository.findTop10ByIdFestivalIdOrderByRatingDesc(festivalId);
	}
}