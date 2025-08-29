package rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Address;
import services.AddressService;

@RestController
@RequestMapping("/api/addresses")
public class AddressRestController {
	@Autowired
	private AddressService addressService;

	@PostMapping("/new")
	public Address createVendor(@RequestBody Address address) {
		return addressService.save(address);
	}

	@GetMapping("/{id}")
	public Address getVendor(@PathVariable Long id) {
		return addressService.getAddressById(id);
	}

	@GetMapping("")
	public List<Address> getAllVendors() {
		return (List<Address>) addressService.getAllAddresses();
	}

	@DeleteMapping("/{id}")
	public Address deleteVendor(@PathVariable Long id) {
		return addressService.deleteAddress(id);
	}
}
