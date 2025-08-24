package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Address;
import lombok.extern.slf4j.Slf4j;
import repository.AddressRepository;

@Slf4j
public class AddressServiceImpl implements AddressService {
	@Autowired
	private AddressRepository addressRepository;

	@Override
	public Iterable<Address> getAllAddresses() {
		return addressRepository.findAll();
	}

	@Override
	public Address getAddressById(Long id) {
		return addressRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
	}

	@Override
	public void save(Address address) {
		addressRepository.save(address);
	}
}