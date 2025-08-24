package repository;

import java.util.List;

import entity.Address;

public interface AddressDao extends GenericDao<Address> {
	List<Address> getAllAddresses();

	void updateAddress(Address address);

	void addAddress(Address address);

	Address getAddressById(Long id);
}
