package services;

import entity.Festival;

public interface FestivalService {

	Iterable<Festival> getAllFestivals();

	Festival getFestivalById(Long id);

	void save(Festival festival);
}
