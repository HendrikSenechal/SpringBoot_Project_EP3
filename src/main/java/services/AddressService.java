package services;

import org.springframework.data.domain.Page;

import entity.Address;

public interface AddressService {
	Page<Address> getAddresses(String search, int page, int size);

	Iterable<Address> getAllAddresses();

	Address getAddressById(Long id);

	Address save(Address address);

	Address deleteAddress(Long id);
}
