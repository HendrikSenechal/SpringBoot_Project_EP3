package domain;

import java.util.List;

import entity.Vendor;

public interface VendorService {

	List<Vendor> getAllVendors();

	Vendor getVendorById(Long id);

	void updateVendor(Vendor vendor);

	void addVendor(Vendor vendor);

	List<Vendor> getVendorsByIds(List<Long> vendorIds);
}
