package domain;

import java.util.List;

import entity.Festival;

public interface FestivalService {

	List<Festival> getAllFestivals();

	Festival getFestivalById(Long id);

	void updateFestival(Festival festival);

	void addFestival(Festival festival);
}
