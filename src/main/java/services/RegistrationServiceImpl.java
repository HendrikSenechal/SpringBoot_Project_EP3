package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Registration;
import repository.RegistrationRepository;

public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegistrationRepository registrationRepository;

	@Override
	public Page<Registration> getRegistrations(Pageable pageable) {
		return registrationRepository.findAll(pageable);
	}
}
