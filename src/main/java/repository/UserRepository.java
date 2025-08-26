package repository;

import org.springframework.data.repository.CrudRepository;

import entity.MyUser;

public interface UserRepository extends CrudRepository<MyUser, Long> {
	MyUser findByEmail(String email);
}
