package services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Address;
import entity.Vendor;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import repository.VendorRepository;

@Slf4j
public class VendorServiceImpl implements VendorService {
	@Autowired
	private VendorRepository vendorRepository;

	@Override
	public Iterable<Vendor> getAllVendors() {
		return vendorRepository.findAll();
	}

	@Override
	public Page<Vendor> getVendors(Pageable pageable) {
		Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase()).and(Sort.by("id").ascending());
		Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		return vendorRepository.findAll(p);
	}

	@Override
	public Page<Vendor> getVendors(Pageable pageable, String search, Long categoryId, Integer minRating) {

		Specification<Vendor> spec = (root, query, cb) -> {
			List<Predicate> preds = new ArrayList<>();

			if (search != null && !search.isBlank()) {
				String like = "%" + search.trim().toLowerCase() + "%";
				Join<Vendor, Address> addr = root.join("address", JoinType.LEFT);
				preds.add(cb.or(cb.like(cb.lower(root.get("name")), like), cb.like(cb.lower(root.get("email")), like),
						cb.like(cb.lower(root.get("phone")), like), cb.like(cb.lower(addr.get("city")), like),
						cb.like(cb.lower(addr.get("country")), like), cb.like(cb.lower(addr.get("street")), like)));
			}

			if (categoryId != null)
				preds.add(cb.equal(root.get("category").get("id"), categoryId));
			if (minRating != null)
				preds.add(cb.greaterThanOrEqualTo(root.get("vendorRating"), minRating));

			return cb.and(preds.toArray(Predicate[]::new));
		};

		Sort defaultSort = Sort.by(Sort.Order.asc("name").ignoreCase());
		Sort sort = pageable.getSort().isUnsorted() ? defaultSort.and(Sort.by("id").ascending())
				: pageable.getSort().and(Sort.by("id").ascending());

		Pageable p = PageRequest.of(Math.max(pageable.getPageNumber(), 0), Math.max(pageable.getPageSize(), 1), sort);

		return vendorRepository.findAll(spec, p);
	}

	@Override
	public Vendor getVendorById(Long id) {
		return vendorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
	}

	@Override
	public void save(Vendor vendor) {
		vendorRepository.save(vendor);
	}

	@Override
	public Iterable<Vendor> getVendorsByIds(List<Long> vendorIds) {
		return vendorRepository.findByIdIn(vendorIds);
	}
}
