package domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import entity.Address;
import entity.Vendor;
import lombok.extern.slf4j.Slf4j;
import services.AddressService;
import services.CategoryService;
import services.VendorService;

@Slf4j
@Controller
public class VendorController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private AddressService addressService;
	@Autowired
	private VendorService vendorService;

	@GetMapping("/vendors")
	public String showVendors(Model model) {
		model.addAttribute("vendors", vendorService.getAllVendors());
		return "vendor-table";
	}

	@GetMapping("/vendors/{id}")
	public String showVendorDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("vendor", vendorService.getVendorById(id));
		return "vendor-details";
	}

	@GetMapping("/vendors/edit/{id}")
	public String editVendor(@PathVariable("id") Long id, Model model) {
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("vendor", vendorService.getVendorById(id));
		model.addAttribute("addresses", addressService.getAllAddresses());
		return "vendor-edit";
	}

	@GetMapping("/vendors/new")
	public String addVendor(Model model) {
		Vendor vendor = new Vendor("", "", "", "", "", "", 0);
		vendor.setAddress(new Address("", "", "", 0, "", 0, ""));
		model.addAttribute("vendor", vendor);
		model.addAttribute("addresses", addressService.getAllAddresses());
		return "vendor-edit";
	}

	@PostMapping("/updateVendor")
	public String onSubmit(@ModelAttribute Vendor vendor, Model model) {
		// Ensure Address is attached as a managed entity
		if (vendor.getAddress() != null && vendor.getAddress().getId() != null) {
			Address address = addressService.getAddressById(vendor.getAddress().getId());
			vendor.setAddress(address);
		}

		// One method for both create & update
		vendorService.save(vendor);

		model.addAttribute("vendors", vendorService.getAllVendors());
		return "vendor-table";
	}

}
