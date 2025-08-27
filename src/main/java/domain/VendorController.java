package domain;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // <-- this one
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String listVendors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "11") int size,
			@RequestParam(required = false) String search, @RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Integer minRating, Model model, Principal principal) {

		model.addAttribute("username", principal.getName());

		Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
		Page<Vendor> vendorPage = vendorService.getVendors(pageable, search, categoryId, minRating);

		model.addAttribute("vendors", vendorPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", vendorPage.getTotalPages());
		model.addAttribute("totalItems", vendorPage.getTotalElements());

		// keep current filters for the view
		model.addAttribute("search", search);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("minRating", minRating);

		// for category dropdown
		model.addAttribute("categories", categoryService.getAllCategories());

		return "vendor-table"; // your template name
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
	public String onSubmit(@ModelAttribute Vendor vendor, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "11") int size, Model model) {
		if (vendor.getAddress() != null && vendor.getAddress().getId() != null) {
			Address address = addressService.getAddressById(vendor.getAddress().getId());
			vendor.setAddress(address);
		}

		vendorService.save(vendor);

		model.addAttribute("vendor", vendorService.getVendorById(vendor.getId()));
		return "vendor-details";
	}

}
