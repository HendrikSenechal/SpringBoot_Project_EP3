package com.springBoot_Opdracht_EP3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import domain.AddressService;
import domain.CategoryService;
import domain.VendorService;
import entity.Address;
import entity.Vendor;
import lombok.extern.slf4j.Slf4j;

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
		Vendor vendor = vendorService.getVendorById(id);
		model.addAttribute("vendor", vendor);
		return "vendor-details";
	}

	@GetMapping("/vendors/edit/{id}")
	public String editVendor(@PathVariable("id") Long id, Model model) {
		Vendor vendor = vendorService.getVendorById(id);
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("vendor", vendor);
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
		Address fullAddress = addressService.getAddressById(vendor.getAddress().getId());
		vendor.setAddress(fullAddress);

		if (vendor.getId() == null) {
			vendorService.addVendor(vendor);
		} else {
			vendorService.updateVendor(vendor);
		}

		model.addAttribute("vendors", vendorService.getAllVendors());
		return "vendor-table";
	}

}
