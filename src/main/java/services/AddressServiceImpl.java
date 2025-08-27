package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import entity.Address;
import jakarta.persistence.criteria.Predicate;
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
	public Page<Address> getAddresses(String search, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").ascending());
		return addressRepository.findAll(textSearch(search), pageable);
	}

	// Search: name, country, place, postcode (int), street
	public static Specification<Address> textSearch(String search) {
		return (root, query, cb) -> {
			if (search == null || search.isBlank())
				return cb.conjunction();
			String s = search.trim();
			String t = "%" + s.toLowerCase() + "%";

			Predicate base = cb.or(cb.like(cb.lower(root.get("name")), t), cb.like(cb.lower(root.get("country")), t),
					cb.like(cb.lower(root.get("place")), t),
					cb.like(root.get("postcode").as(String.class), "%" + s + "%"),
					cb.like(cb.lower(root.get("street")), t));

			return s.chars().allMatch(Character::isDigit)
					? cb.or(base, cb.equal(root.get("postcode"), Integer.valueOf(s)))
					: base;
		};
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