package repository;

import java.util.List;

import entity.User;

public interface UserDao extends GenericDao<User> {
	List<User> getAllUsers();

	void updateUser(User user);

	void addUser(User User);
}
