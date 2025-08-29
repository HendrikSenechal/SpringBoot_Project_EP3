package services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;

public interface RegistrationService {
	Page<Registration> getRegistrations(Long festivalId, Pageable pageable);

	Registration getRegistrationById(Long festivalId, Long UserId);

	void buyTickets(Long userId, Long festivalId, int tickets);

	Double getAverageRatingForFestival(Long festivalId);

	List<Registration> getTop10Reviews(Long festivalId);

	Registration saveOrUpdate(Registration incoming);

	int getTicketsByFestival(Long festivalId);

	int getTicketsByFestivalAndUser(Long festivalId, Long userId);

	void updateTickets(Long festivalId, Long userId, Integer tickets);

	Registration deleteById(Long festivalId, Long userId);

	List<Registration> getAllRegistrations();
}
