package com.springBoot_Opdracht_EP3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import entity.Address;
import lombok.extern.slf4j.Slf4j;
import services.AddressService;

@Slf4j
@Controller
public class AddressController {

	@Autowired
	private AddressService addressesService;

	@GetMapping("/addresses")
	public String showAddresses(Model model) {
		model.addAttribute("addresses", addressesService.getAllAddresses());
		return "address-table";
	}

	@GetMapping("/addresses/{id}")
	public String showAddressDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("address", addressesService.getAddressById(id));
		return "address-details";
	}

	@GetMapping("/addresses/edit/{id}")
	public String editAddress(@PathVariable("id") Long id, Model model) {
		model.addAttribute("address", addressesService.getAddressById(id));
		return "address-edit";
	}

	@GetMapping("/address/new")
	public String addAddress(Model model) {
		Address address = new Address("", "", "", 0, "", 0, "");
		model.addAttribute("address", address);
		return "address-edit";
	}

	@PostMapping("/updateAddress")
	public String onSubmit(Address address, Model model) {
		addressesService.save(address);
		model.addAttribute("addresses", addressesService.getAllAddresses());
		return "address-table";
	}
}
