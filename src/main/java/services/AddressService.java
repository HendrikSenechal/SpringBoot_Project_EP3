package services;

import entity.Address;

public interface AddressService {

	Iterable<Address> getAllAddresses();

	Address getAddressById(Long id);

	void save(Address address);
}
