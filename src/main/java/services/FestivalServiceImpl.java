package services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import entity.Festival;
import jakarta.persistence.criteria.JoinType;
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
	public Page<Festival> getFestivals(Pageable pageable, String search, Long categoryId, String status) {
		Specification<Festival> spec = Specification.where(null);

		// search by name OR address.place (location)
		if (search != null && !search.isBlank()) {
			final String like = "%" + search.trim().toLowerCase() + "%";
			spec = spec.and((root, cq, cb) -> cb.or(cb.like(cb.lower(root.get("name")), like),
					cb.like(cb.lower(root.join("address", JoinType.LEFT).get("place")), like)));
		}

		// filter by category id
		if (categoryId != null) {
			spec = spec.and((root, cq, cb) -> cb.equal(root.join("category", JoinType.LEFT).get("id"), categoryId));
		}

		// filter by time status (ALL / UPCOMING / PAST) using 'start' field
		if (status != null && !"ALL".equalsIgnoreCase(status)) {
			LocalDateTime now = LocalDateTime.now();
			if ("UPCOMING".equalsIgnoreCase(status)) {
				spec = spec.and((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("start"), now));
			} else if ("PAST".equalsIgnoreCase(status)) {
				spec = spec.and((root, cq, cb) -> cb.lessThan(root.get("start"), now));
			}
		}

		return festivalRepository.findAll(spec, pageable);
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
