package services;

import java.util.List;

import entity.Vendor;

public interface VendorService {

	Iterable<Vendor> getAllVendors();

	Vendor getVendorById(Long id);

	void save(Vendor vendor);

	Iterable<Vendor> getVendorsByIds(List<Long> vendorIds);
}
