package services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
		return vendorRepository.findAll(pageable);
	}

	@Override
	public Page<Vendor> getVendors(Pageable pageable, String search, Long categoryId, Integer minRating) {

		Specification<Vendor> spec = (root, query, cb) -> {
			List<Predicate> preds = new ArrayList<>();

			// SEARCH: name, email, phone, location (city/country/street)
			if (search != null && !search.isBlank()) {
				String like = "%" + search.trim().toLowerCase() + "%";
				Join<Vendor, Address> addr = root.join("address", JoinType.LEFT);

				preds.add(cb.or(cb.like(cb.lower(root.get("name")), like), cb.like(cb.lower(root.get("email")), like),
						cb.like(cb.lower(root.get("phone")), like), cb.like(cb.lower(addr.get("city")), like),
						cb.like(cb.lower(addr.get("country")), like), cb.like(cb.lower(addr.get("street")), like)));
			}

			// FILTER: category
			if (categoryId != null) {
				preds.add(cb.equal(root.get("category").get("id"), categoryId));
			}

			// FILTER: rating (use vendorRating, not rating)
			if (minRating != null) {
				preds.add(cb.greaterThanOrEqualTo(root.get("vendorRating"), minRating));
			}

			return cb.and(preds.toArray(new Predicate[0]));
		};

		return vendorRepository.findAll(spec, pageable);
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
