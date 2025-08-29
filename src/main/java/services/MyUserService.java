package services;

import java.util.List;

import entity.MyUser;

public interface MyUserService {
	MyUser createFestival(MyUser user);

	MyUser getUser(Long id);

	List<MyUser> getAllFestivals();

	MyUser deleteFestival(Long id);
}
