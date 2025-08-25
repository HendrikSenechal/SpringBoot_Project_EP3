package services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Festival;

public interface FestivalService {

	Page<Festival> getFestivals(Pageable pageable);

	Festival getFestivalById(Long id);

	void save(Festival festival);
}
