package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
		return addressRepository.findAll(Sort.by(Order.asc("name").ignoreCase(), Order.asc("id")));
	}

	@Override
	public Page<Address> getAddresses(String search, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
				Sort.by(Order.asc("name").ignoreCase(), Order.asc("id")));
		return addressRepository.findAll(textSearch(search), pageable);
	}

	public static Specification<Address> textSearch(String search) {
		return (root, query, cb) -> {
			if (search == null || search.isBlank()) {
				// default sort when no search too
				if (query.getOrderList() == null || query.getOrderList().isEmpty()) {
					query.orderBy(cb.asc(cb.lower(root.get("name"))), cb.asc(root.get("id")));
				}
				return cb.conjunction();
			}

			String s = search.trim();
			String t = "%" + s.toLowerCase() + "%";

			Predicate base = cb.or(cb.like(cb.lower(root.get("name")), t), cb.like(cb.lower(root.get("country")), t),
					cb.like(cb.lower(root.get("place")), t),
					cb.like(root.get("postcode").as(String.class), "%" + s + "%"),
					cb.like(cb.lower(root.get("street")), t));

			Predicate p = s.chars().allMatch(Character::isDigit)
					? cb.or(base, cb.equal(root.get("postcode"), Integer.valueOf(s)))
					: base;

			// Apply default sort if caller didn't pass a Sort via Pageable
			if (query.getOrderList() == null || query.getOrderList().isEmpty()) {
				query.orderBy(cb.asc(cb.lower(root.get("name"))), cb.asc(root.get("id")));
			}

			return p;
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