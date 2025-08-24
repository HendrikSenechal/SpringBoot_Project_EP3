package repository;

import java.util.List;

import entity.Festival;

public interface FestivalDao extends GenericDao<Festival> {
	List<Festival> getAllFestivals();

	Festival getFestivalById(Long id);

	void updateFestival(Festival festival);

	void addFestival(Festival festival);

	List<Festival> getFestivalsPaginated(int page, int pageSize);

	long countFestivals();
}
