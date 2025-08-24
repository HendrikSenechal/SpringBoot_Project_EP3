package com.springBoot_Opdracht_EP3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import domain.AddressService;
import domain.CategoryService;
import domain.FestivalService;
import domain.VendorService;
import entity.Address;
import entity.Festival;
import entity.Vendor;
import lombok.extern.slf4j.Slf4j;

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
			@RequestParam(value = "vendorIds", required = false) List<Long> vendorIds, Model model) {

		// Resolve address if present
		if (festival.getAddress() != null && festival.getAddress().getId() != null) {
			Address fullAddress = addressService.getAddressById(festival.getAddress().getId());
			festival.setAddress(fullAddress);
		}

		// Resolve vendors if selected
		if (vendorIds != null && !vendorIds.isEmpty()) {
			Set<Vendor> selectedVendors = new HashSet<>(vendorService.getVendorsByIds(vendorIds));
			festival.setVendors(selectedVendors);
		} else {
			festival.setVendors(new HashSet<>()); // no vendors selected
		}

		// Decide whether to create or update
		if (festival.getId() == null) {
			festivalService.addFestival(festival); // assumes such method exists
		} else {
			festivalService.updateFestival(festival);
		}

		model.addAttribute("festivals", festivalService.getAllFestivals());
		return "festival-table";
	}

}