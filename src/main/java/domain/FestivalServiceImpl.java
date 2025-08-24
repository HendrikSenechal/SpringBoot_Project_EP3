package domain;

import java.util.List;

import entity.Festival;
import repository.FestivalDaoJpa;

public class FestivalServiceImpl implements FestivalService {
	FestivalDaoJpa festivalRepository = new FestivalDaoJpa();

	@Override
	public List<Festival> getAllFestivals() {
		List<Festival> allFestivals = festivalRepository.getAllFestivals();
		return allFestivals/* .subList(0, Math.min(11, allFestivals.size())) */;
	}

	@Override
	public Festival getFestivalById(Long id) {
		return festivalRepository.getFestivalById(id);
	}

	@Override
	public void updateFestival(Festival festival) {
		festivalRepository.update(festival);
	}

	@Override
	public void addFestival(Festival festival) {
		festivalRepository.addFestival(festival);
	}

}
