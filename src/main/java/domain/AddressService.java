package domain;

import java.util.List;

import entity.Address;

public interface AddressService {

	List<Address> getAllAddresses();

	Address getAddressById(Long id);

	void updateAddress(Address address);

	void addAddress(Address address);
}
