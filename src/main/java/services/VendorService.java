package services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import entity.Vendor;

public interface VendorService {

	Iterable<Vendor> getAllVendors();

	Vendor getVendorById(Long id);

	Page<Vendor> getVendors(Pageable pageable);

	Page<Vendor> getVendors(Pageable pageable, String search, Long categoryId, Integer minRating);

	void save(Vendor vendor);
}
