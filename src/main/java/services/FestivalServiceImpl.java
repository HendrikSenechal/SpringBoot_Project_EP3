package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import entity.Festival;
import repository.FestivalRepository;

@Service
@Transactional
public class FestivalServiceImpl implements FestivalService {
	@Autowired
	private FestivalRepository festivalRepository;

	@Override
	public Page<Festival> getFestivals(Pageable pageable) {
		return festivalRepository.findAll(pageable);
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
