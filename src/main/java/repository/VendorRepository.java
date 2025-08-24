package repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import entity.Vendor;

public interface VendorRepository extends CrudRepository<Vendor, Long> {
	List<Vendor> findByIdIn(List<Long> ids);
}
