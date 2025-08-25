package services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;

public interface RegistrationService {
	Page<Registration> getRegistrations(Pageable pageable);

}
