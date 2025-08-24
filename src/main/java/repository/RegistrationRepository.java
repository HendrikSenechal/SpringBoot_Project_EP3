package repository;

import org.springframework.data.repository.CrudRepository;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationRepository extends CrudRepository<Registration, UserFestivalKey> {

}
