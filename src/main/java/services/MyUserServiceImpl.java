package services;

import java.util.List;

import org.springframework.stereotype.Service;

import entity.MyUser;
import exception.EntityNotFoundException;
import repository.UserRepository;

@Service
public class MyUserServiceImpl implements MyUserService {
	private UserRepository userRepositroy;

	public MyUser createFestival(MyUser user) {
		userRepositroy.save(user);
		return user;
	}

	public MyUser getUser(Long id) {
		return userRepositroy.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
	}

	public List<MyUser> getAllFestivals() {
		return userRepositroy.findAll();
	}

	public MyUser deleteFestival(Long id) {
		MyUser user = userRepositroy.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
		userRepositroy.delete(user);
		return user;
	}
}
