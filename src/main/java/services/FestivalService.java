package services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Festival;

public interface FestivalService {

	Page<Festival> getFestivals(Pageable pageable);

	Page<Festival> getFestivals(Pageable pageable, String search, Long categoryId, String status);

	Festival getFestivalById(Long id);

	void save(Festival festival, List<Long> vendorIds);
}
