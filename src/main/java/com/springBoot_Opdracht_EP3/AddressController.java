package com.springBoot_Opdracht_EP3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import domain.AddressService;
import entity.Address;
import lombok.extern.slf4j.Slf4j;

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
		Address address = addressesService.getAddressById(id);
		model.addAttribute("address", address);
		return "address-details";
	}

	@GetMapping("/addresses/edit/{id}")
	public String editAddress(@PathVariable("id") Long id, Model model) {
		Address address = addressesService.getAddressById(id);
		model.addAttribute("address", address);
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
		if (address.getId() == null) {
			addressesService.addAddress(address);
		} else {
			addressesService.updateAddress(address);
		}

		List<Address> addresses = addressesService.getAllAddresses();
		model.addAttribute("addresses", addresses);
		return "address-table";
	}
}
