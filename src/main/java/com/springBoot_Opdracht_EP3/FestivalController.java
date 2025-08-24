package com.springBoot_Opdracht_EP3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entity.Festival;
import lombok.extern.slf4j.Slf4j;
import services.AddressService;
import services.CategoryService;
import services.FestivalService;
import services.VendorService;

@Slf4j
@Controller
public class FestivalController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private AddressService addressService;
	@Autowired
	private VendorService vendorService;
	@Autowired
	private FestivalService festivalService;

	@GetMapping("/festivals")
	public String showFestivals(Model model) {
		model.addAttribute("festivals", festivalService.getAllFestivals());
		return "festival-table"; // name of the HTML file (festival-table.html)
	}

	@GetMapping("/festivals/{id}")
	public String showFestivalDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("festival", festivalService.getFestivalById(id));
		return "festival-details"; // Your Thymeleaf template
	}

	@GetMapping("/festivals/edit/{id}")
	public String editFestival(@PathVariable("id") Long id, Model model) {
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("addresses", addressService.getAllAddresses());
		model.addAttribute("vendors", vendorService.getAllVendors());
		model.addAttribute("festival", festivalService.getFestivalById(id));
		return "festival-edit";
	}

	@GetMapping("/festivals/new")
	public String newFestival(Model model) {
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("addresses", addressService.getAllAddresses());
		model.addAttribute("vendors", vendorService.getAllVendors());
		model.addAttribute("festival", new Festival("", "", 0, 0));
		return "festival-edit";
	}

	@PostMapping("/updateFestival")
	public String saveOrUpdateFestival(@ModelAttribute Festival festival,
			@RequestParam(value = "vendorIds", required = false) java.util.List<Long> vendorIds, Model model) {

		// Make sure Address is a managed entity
		if (festival.getAddress() != null && festival.getAddress().getId() != null) {
			entity.Address fullAddress = addressService.getAddressById(festival.getAddress().getId());
			festival.setAddress(fullAddress);
		}

		// Map selected vendor IDs -> Vendor entities
		java.util.Set<entity.Vendor> selectedVendors = new java.util.HashSet<>();
		if (vendorIds != null && !vendorIds.isEmpty()) {
			for (entity.Vendor v : vendorService.getVendorsByIds(vendorIds)) {
				selectedVendors.add(v);
			}
		}
		festival.setVendors(selectedVendors);

		// One method for both create & update
		festivalService.save(festival);

		model.addAttribute("festivals", festivalService.getAllFestivals());
		return "festival-table";
	}

}