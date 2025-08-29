package rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.MyUser;
import services.MyUserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	@Autowired
	private MyUserService myUserService;

	@PostMapping("/new")
	public MyUser createUser(@RequestBody MyUser myUser) {
		return myUserService.createFestival(myUser);
	}

	@GetMapping("/{id}")
	public MyUser getUser(@PathVariable Long id) {
		return myUserService.getUser(id);
	}

	@GetMapping("")
	public List<MyUser> getAllUsers() {
		return (List<MyUser>) myUserService.getAllFestivals();
	}

	@DeleteMapping("/{id}")
	public MyUser deleteVendor(@PathVariable Long id) {
		return myUserService.deleteFestival(id);
	}
}
