package repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entity.Address;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

public class AddressDaoJpa extends GenericDaoJpa<Address> implements AddressDao {

	private static final Logger log = LoggerFactory.getLogger(AddressDaoJpa.class);

	public AddressDaoJpa() {
		super(Address.class);
		log.info("AddressDaoJpa initialized");
	}

	public List<Address> getAllAddresses() {
		log.info("Started fetching all addresses");
		try {
			List<Address> addresses = em.createNamedQuery("Address.findAll", Address.class).getResultList();
			log.info("Fetched {} addresses", addresses.size());
			return addresses;
		} catch (NoResultException ex) {
			log.warn("No addresses found", ex);
			throw new EntityNotFoundException("No addresses found");
		}
	}

	@Override
	public void updateAddress(Address address) {
		log.info("Updating address with ID {}", address.getId());
		runInTransaction(() -> {
			super.update(address);
			log.info("Address with ID {} updated", address.getId());
		});
	}

	@Override
	public void addAddress(Address address) {
		log.info("Adding new address");
		runInTransaction(() -> {
			super.insert(address);
			log.info("New address added");
		});
	}

	@Override
	public Address getAddressById(Long id) {
		log.info("Fetching address with id {}", id);
		try {
			Address address = em.createNamedQuery("Address.findById", Address.class).setParameter("id", id)
					.getSingleResult();
			log.info("Fetched {} address", address.getId());
			return address;
		} catch (NoResultException ex) {
			log.warn("No address found");
			throw new EntityNotFoundException("No address found");
		}
	}
}
