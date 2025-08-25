package com.springBoot_Opdracht_EP3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entity.Festival;
import entity.Registration;
import lombok.extern.slf4j.Slf4j;
import services.AddressService;
import services.CategoryService;
import services.FestivalService;
import services.RegistrationService;
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
	@Autowired
	private RegistrationService registrationService;

	@GetMapping("/festivals")
	public String listFestivals(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			Model model) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Festival> festivalPage = festivalService.getFestivals(pageable);

		model.addAttribute("festivals", festivalPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", festivalPage.getTotalPages());
		model.addAttribute("totalItems", festivalPage.getTotalElements());

		return "festival-table";
	}

	@GetMapping("/festivals/{id}")
	public String showFestivalDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("festival", festivalService.getFestivalById(id));
		model.addAttribute("averageRating", registrationService.getAverageRatingForFestival(id));

		Pageable pageable = PageRequest.of(0, 10);
		Page<Registration> registrationPage = registrationService.getRegistrations(id, pageable);

		model.addAttribute("registrations", registrationPage.getContent());
		model.addAttribute("currentPage", 0);
		model.addAttribute("pageSize", 10);
		model.addAttribute("totalPages", registrationPage.getTotalPages());
		model.addAttribute("totalItems", registrationPage.getTotalElements());

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

		// after festivalService.save(festival);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Festival> page = festivalService.getFestivals(pageable);

		model.addAttribute("festivals", page.getContent());
		model.addAttribute("currentPage", page.getNumber());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("pageSize", page.getSize());

		return "festival-table";
	}

}