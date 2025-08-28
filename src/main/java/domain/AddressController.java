package domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entity.Address;
import lombok.extern.slf4j.Slf4j;
import services.AddressService;

@Slf4j
@Controller
public class AddressController {

	@Autowired
	private AddressService addressesService;

	@GetMapping("/addresses")
	public String listAddresses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "11") int size,
			@RequestParam(required = false) String search, Model model) {
		Page<Address> p = addressesService.getAddresses(search, page, size);

		long total = p.getTotalElements();
		int start = total == 0 ? 0 : (p.getNumber() * p.getSize()) + 1;
		int end = total == 0 ? 0 : start + p.getNumberOfElements() - 1;

		model.addAttribute("addresses", p.getContent());
		model.addAttribute("currentPage", p.getNumber());
		model.addAttribute("totalPages", p.getTotalPages());
		model.addAttribute("pageSize", p.getSize());
		model.addAttribute("totalElements", total);
		model.addAttribute("showingFrom", start);
		model.addAttribute("showingTo", end);
		model.addAttribute("search", search);

		return "address-table"; // update if your template name differs
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
		model.addAttribute("address", new Address("", "", "", 0, "", 0, ""));
		return "address-edit";
	}

	@PostMapping("/updateAddress")
	public String onSubmit(Address address, Model model) {
		addressesService.save(address);
		model.addAttribute("address", addressesService.getAddressById(address.getId()));
		return "address-details";
	}
}
