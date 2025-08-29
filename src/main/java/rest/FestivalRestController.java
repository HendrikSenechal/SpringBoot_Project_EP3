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

import entity.Festival;
import services.FestivalService;

@RestController
@RequestMapping("/api/festivals")
public class FestivalRestController {
	@Autowired
	private FestivalService festivalService;

	@PostMapping("/new")
	public Festival createFestival(@RequestBody Festival festival) {
		// If you want to set createdDateTime here, do it if your entity has that field:
		// f.setCreatedDateTime(LocalDateTime.now());
		return festivalService.createFestival(festival);
	}

	@GetMapping("/{id}")
	public Festival getFestival(@PathVariable Long id) {
		return festivalService.getFestivalById(id);
	}

	@GetMapping("")
	public List<Festival> getAllFestivals() {
		return festivalService.getAllFestivals();
	}

	@DeleteMapping("/{id}")
	public Festival deleteFestival(@PathVariable Long id) {
		return festivalService.deleteFestival(id);
	}
}
