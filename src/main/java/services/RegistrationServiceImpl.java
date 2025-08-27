package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import entity.Registration;
import lombok.extern.slf4j.Slf4j;
import repository.RegistrationRepository;
import security.CustomUserDetails;

@Slf4j
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
	public Registration getRegistrationById(Long festivalId, Long userId) {
		return registrationRepository.findByFestivalIdAndMyUserId(festivalId, userId).orElse(null);
	}

	@Override
	public Double getAverageRatingForFestival(Long festivalId) {
		Double avg = registrationRepository.findAverageRatingByFestivalId(festivalId);
		return (avg == null) ? 0.0 : Math.round(avg * 10.0) / 10.0;
	}

	@Override
	public List<Registration> getTop10Reviews(Long festivalId) {
		return registrationRepository.findTop10ByIdFestivalIdOrderByRatingDesc(festivalId);
	}

	@Transactional
	public void saveOrUpdate(Registration form) {
		registrationRepository.save(form);
	}

	public int getTicketsByFestival(Long festivalId) {
		return registrationRepository.sumTicketsForFestival(festivalId);
	}

	public int getTicketsByFestivalAndUser(Long festivalId, Long userId) {
		Long sum = registrationRepository.sumTicketsForFestivalAndCurrentUser(festivalId, userId);
		return (sum == null) ? 0 : Math.toIntExact(sum); // throws if overflow
	}

	public void updateTickets(Long festivalId, Long userId, Integer tickets) {
		registrationRepository.updateTickets(festivalId, userId, tickets);
	}

	public void deleteById(Long festivalId, Long userId) {
		registrationRepository.deleteByFestivalIdAndUserId(festivalId, userId);
	}
}
