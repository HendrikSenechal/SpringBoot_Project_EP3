package rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.Registration;
import services.RegistrationService;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationRestController {
	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/new")
	public Registration createVendor(@RequestBody Registration registration) {
		return registrationService.saveOrUpdate(registration);
	}

	@GetMapping("/{id}")
	public Registration getVendor(@RequestParam Long festivalId, @RequestParam Long userId) {
		return registrationService.getRegistrationById(festivalId, userId);
	}

	@GetMapping("")
	public List<Registration> getAllVendors() {
		return registrationService.getAllRegistrations();
	}

	@DeleteMapping("/{id}")
	public Registration deleteRegistration(@RequestParam Long festivalId, @RequestParam Long userId) {
		return registrationService.deleteById(festivalId, userId);
	}
}
