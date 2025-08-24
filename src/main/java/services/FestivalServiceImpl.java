package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Festival;
import repository.FestivalRepository;

public class FestivalServiceImpl implements FestivalService {
	@Autowired
	private FestivalRepository festivalRepository;

	@Override
	public Iterable<Festival> getAllFestivals() {
		return festivalRepository.findAll();
	}

	@Override
	public Festival getFestivalById(Long id) {
		return festivalRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));
	}

	@Override
	public void save(Festival festival) {
		festivalRepository.save(festival);
	}
}
