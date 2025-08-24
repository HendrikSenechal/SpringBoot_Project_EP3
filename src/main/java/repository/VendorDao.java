package repository;

import java.util.List;

import entity.Vendor;

public interface VendorDao extends GenericDao<Vendor> {
	List<Vendor> getAllVendors();

	void updateVendor(Vendor vendor);

	void addVendor(Vendor vendor);

	Vendor getVendorById(Long id);

	List<Vendor> getVendorsByIds(List<Long> vendorIds);

}
