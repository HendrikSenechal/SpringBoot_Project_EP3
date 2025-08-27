package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.MyUser;

public interface UserRepository extends JpaRepository<MyUser, Long> {
	MyUser findByEmail(String email);
}
