package domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import entity.Address;
import entity.Vendor;
import lombok.extern.slf4j.Slf4j;
import repository.AddressDaoJpa;
import repository.VendorDaoJpa;

@Slf4j
public class VendorServiceImpl implements VendorService {
	@Autowired
	private VendorDaoJpa vendorRepository;
	@Autowired
	private AddressDaoJpa addressRepository;

	@Override
	public List<Vendor> getAllVendors() {
		return vendorRepository.getAllVendors();
	}

	@Override
	public Vendor getVendorById(Long id) {
		System.out.println(id);
		return vendorRepository.getVendorById(id);
	}

	@Override
	public void updateVendor(Vendor vendor) {
		Address existingAddress = addressRepository.getAddressById(vendor.getAddress().getId());
		vendor.setAddress(existingAddress);
		vendorRepository.updateVendor(vendor);
	}

	@Override
	public void addVendor(Vendor vendor) {
		vendorRepository.updateVendor(vendor);
	}

	@Override
	public List<Vendor> getVendorsByIds(List<Long> vendorIds) {
		return vendorRepository.getVendorsByIds(vendorIds);
	}

}
