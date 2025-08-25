package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Registration;
import entity.UserFestivalKey;

public interface RegistrationRepository extends JpaRepository<Registration, UserFestivalKey> {

}
