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
import exception.EntityNotFoundException;
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

	@Transactional
	public void save(Festival form, List<Long> vendorIds) {
		LocalDateTime start = form.getStart();
		LocalDateTime end = form.getEnd();

		if (start == null || end == null || end.isBefore(start)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period.");
		}

		boolean exists;
		if (form.getId() == null) {
			exists = festivalRepository.existsOverlapByName(form.getName(), start, end);
		} else {
			exists = festivalRepository.existsOverlapByNameExcludingId(form.getName(), start, end, form.getId());
		}

		if (exists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Another festival with this name exists in the selected period.");
		}

		Address fullAddress = addressRepository.findById(form.getAddress().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
		form.setAddress(fullAddress);

		if (form.getId() != null) {
			Festival existing = festivalRepository.findById(form.getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));

			existing.setName(form.getName());
			existing.setDescription(form.getDescription());
			existing.setStart(form.getStart());
			existing.setEnd(form.getEnd());
			existing.setPrice(form.getPrice());
			existing.setAmount(form.getAmount());
			existing.setFestivalCode1(form.getFestivalCode1());
			existing.setFestivalCode2(form.getFestivalCode2());
			existing.setAddress(form.getAddress());
			existing.setCategory(form.getCategory());

			existing.getVendors().clear();
			if (vendorIds != null && !vendorIds.isEmpty()) {
				existing.getVendors().addAll(vendorRepository.findByIdIn(vendorIds));
			}

			festivalRepository.save(existing);
		}

		if (vendorIds != null && !vendorIds.isEmpty()) {
			form.setVendors(new HashSet<>(vendorRepository.findByIdIn(vendorIds)));
		}
		festivalRepository.save(form);
	}

	@Override
	public Festival addVendors(Festival festival, List<Long> vendorIds) {
		Set<Vendor> selectedVendors = new HashSet<>();
		if (vendorIds != null && !vendorIds.isEmpty()) {
			for (entity.Vendor v : vendorRepository.findByIdIn(vendorIds)) {
				selectedVendors.add(v);
			}
		}
		festival.setVendors(selectedVendors);
		return festival;
	}

	// REST

	public Festival createFestival(Festival festival) {

		return festivalRepository.save(festival);
	}

	public List<Festival> getAllFestivals() {
		return festivalRepository.findAll();
	}

	public Festival deleteFestival(Long id) {
		Festival festival = festivalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
		festivalRepository.delete(festival);
		return festival;
	}

}
