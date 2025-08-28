package services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import entity.Address;
import entity.Festival;
import entity.Vendor;
import jakarta.persistence.criteria.JoinType;
import repository.AddressRepository;
import repository.FestivalRepository;
import repository.VendorRepository;

@Service
@Transactional
public class FestivalServiceImpl implements FestivalService {
	@Autowired
	private FestivalRepository festivalRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private VendorRepository vendorRepository;

	@Override
	public Page<Festival> getFestivals(Pageable pageable) {
		Sort sort = pageable.getSort().isUnsorted()
				? Sort.by(Order.asc("start").nullsLast()).and(Sort.by("id").ascending())
				: pageable.getSort().and(Sort.by("id").ascending());
		Pageable p = PageRequest.of(pageable.getPageNumber(), Math.max(pageable.getPageSize(), 1), sort);
		return festivalRepository.findAll(p);
	}

	// 2) Filtered list — same default sort by start
	@Override
	public Page<Festival> getFestivals(Pageable pageable, String search, Long categoryId, String status) {
		Specification<Festival> spec = Specification.where(null);

		if (search != null && !search.isBlank()) {
			final String like = "%" + search.trim().toLowerCase() + "%";
			spec = spec.and((root, cq, cb) -> cb.or(cb.like(cb.lower(root.get("name")), like),
					cb.like(cb.lower(root.join("address", JoinType.LEFT).get("place")), like)));
		}

		if (categoryId != null) {
			spec = spec.and((root, cq, cb) -> cb.equal(root.join("category", JoinType.LEFT).get("id"), categoryId));
		}

		if (status != null && !"ALL".equalsIgnoreCase(status)) {
			LocalDateTime now = LocalDateTime.now();
			if ("UPCOMING".equalsIgnoreCase(status)) {
				spec = spec.and((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("start"), now));
			} else if ("PAST".equalsIgnoreCase(status)) {
				spec = spec.and((root, cq, cb) -> cb.lessThan(root.get("start"), now));
			}
		}

		Sort sort = pageable.getSort().isUnsorted()
				? Sort.by(Order.asc("start").nullsLast()).and(Sort.by("id").ascending())
				: pageable.getSort().and(Sort.by("id").ascending());
		Pageable p = PageRequest.of(pageable.getPageNumber(), Math.max(pageable.getPageSize(), 1), sort);

		return festivalRepository.findAll(spec, p);
	}

	@Override
	public Festival getFestivalById(Long id) {
		return festivalRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));
	}

	@Override
	public void save(Festival festival, List<Long> vendorIds) {
		if (festival.getAddress() != null && festival.getAddress().getId() != null) {
			Address fullAddress = addressRepository.findById(festival.getAddress().getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
			festival.setAddress(fullAddress);
		}

		Set<Vendor> selectedVendors = new HashSet<>();
		if (vendorIds != null && !vendorIds.isEmpty()) {
			for (entity.Vendor v : vendorRepository.findByIdIn(vendorIds)) {
				selectedVendors.add(v);
			}
		}
		festival.setVendors(selectedVendors);

		festivalRepository.save(festival);
	}

}
