package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Vendor;
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
