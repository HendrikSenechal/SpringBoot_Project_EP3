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

import entity.Vendor;
import services.VendorService;

@RestController
@RequestMapping("/api/vendors")
public class VendorRestController {
	@Autowired
	private VendorService vendorService;

	@PostMapping("/new")
	public Vendor createVendor(@RequestBody Vendor vendor) {
		return vendorService.createVendor(vendor);
	}

	@GetMapping("/{id}")
	public Vendor getVendor(@PathVariable Long id) {
		return vendorService.getVendorById(id);
	}

	@GetMapping("")
	public List<Vendor> getAllVendors() {
		return (List<Vendor>) vendorService.getAllVendors();
	}

	@DeleteMapping("/{id}")
	public Vendor deleteVendor(@PathVariable Long id) {
		return vendorService.deleteVendor(id);
	}
}
