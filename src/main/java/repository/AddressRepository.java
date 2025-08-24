package repository;

import org.springframework.data.repository.CrudRepository;

import entity.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {

}
