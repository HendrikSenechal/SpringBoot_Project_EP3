package services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import entity.Festival;
import entity.MyUser;
import entity.Registration;
import lombok.extern.slf4j.Slf4j;
import repository.FestivalRepository;
import repository.RegistrationRepository;
import repository.UserRepository;
import security.CustomUserDetails;

@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FestivalRepository festivalRepository;

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
		List<Registration> all = registrationRepository.findTop10ByIdFestivalIdOrderByRatingDesc(festivalId);
		return all.subList(0, Math.min(10, all.size()));
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

	public void buyTickets(Long userId, Long festivalId, int tickets) {
		Registration registration = registrationRepository.findByFestivalIdAndMyUserId(festivalId, userId).orElse(null);

		MyUser myUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		Festival festival = festivalRepository.findById(festivalId)
				.orElseThrow(() -> new RuntimeException("Festival not found with id: " + festivalId));

		if (registration == null) {
			registration = new Registration(null, tickets, "", "", LocalDateTime.now(), myUser, festival);
		}

		registration.setTickets(tickets);
		registrationRepository.save(registration);
	}

}
