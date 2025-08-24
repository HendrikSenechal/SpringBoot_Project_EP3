package domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import entity.Address;
import lombok.extern.slf4j.Slf4j;
import repository.AddressDaoJpa;

@Slf4j
public class AddressServiceImpl implements AddressService {
	@Autowired
	private AddressDaoJpa addressRepository;

	@Override
	public List<Address> getAllAddresses() {
		return addressRepository.getAllAddresses();
	}

	@Override
	public Address getAddressById(Long id) {
		return addressRepository.getAddressById(id);
	}

	@Override
	public void updateAddress(Address address) {
		addressRepository.updateAddress(address);
	}

	@Override
	public void addAddress(Address address) {
		addressRepository.addAddress(address);
	}
}