package repository;

import java.util.List;

import entity.Registration;

public interface RegistrationDao extends GenericDao<Registration> {
	List<Registration> getAllRegistrations();

	void updateRegistration(Registration registration);

	void addRegistration(Registration registration);
}
