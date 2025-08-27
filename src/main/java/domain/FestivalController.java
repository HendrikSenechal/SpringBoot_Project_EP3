package domain;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entity.Festival;
import entity.Registration;
import entity.UserFestivalKey;
import lombok.extern.slf4j.Slf4j;
import repository.FestivalRepository;
import repository.UserRepository;
import security.CustomUserDetails;
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
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FestivalRepository festivalRepository;

	@GetMapping("/festivals")
	public String listFestivals(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "11") int size,
			Model model, Principal principal) {
		model.addAttribute("username", principal.getName());

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
	public String showFestivalDetails(@PathVariable("id") Long id, Authentication authentication, Model model) {
		model.addAttribute("festival", festivalService.getFestivalById(id));
		model.addAttribute("averageRating", registrationService.getAverageRatingForFestival(id));
		model.addAttribute("reservedTickets", registrationService.getTicketsByFestival(id));
		model.addAttribute("currentDate", LocalDate.now()); // <-- Add this line

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("userTickets", registrationService.getTicketsByFestivalAndUser(id, userDetails.getId()));

		Pageable pageable = PageRequest.of(0, 10);
		List<Registration> top10Reviews = registrationService.getTop10Reviews(id);

		model.addAttribute("registrations", top10Reviews);

		return "festival-details";
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

	// REVIEW

	private void populateFestivalReviewModel(Long festivalId, int page, int size, CustomUserDetails user, Model model) {
		model.addAttribute("userReview", registrationService.getRegistrationById(festivalId, user.getId()));
		model.addAttribute("festival", festivalService.getFestivalById(festivalId));
		model.addAttribute("averageRating", registrationService.getAverageRatingForFestival(festivalId));

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
		Page<Registration> registrationPage = registrationService.getRegistrations(festivalId, pageable);

		model.addAttribute("registrations", registrationPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", registrationPage.getTotalPages());
		model.addAttribute("totalItems", registrationPage.getTotalElements());
		model.addAttribute("festivalId", festivalId);
	}

	@GetMapping("/festivals/{id}/reviews")
	public String showFestivalReviews(@PathVariable("id") Long festivalId, @RequestParam(defaultValue = "0") int page,
			@AuthenticationPrincipal CustomUserDetails user, @RequestParam(defaultValue = "10") int size, Model model) {
		model.addAttribute("editedMessage", "");
		populateFestivalReviewModel(festivalId, page, size, user, model);

		return "festival-review";
	}

	@PostMapping("/festivals/{festivalId}/reviews")
	public String saveFestivalReview(@PathVariable("festivalId") Long festivalId,
			@AuthenticationPrincipal CustomUserDetails user, @ModelAttribute("registration") Registration form,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, Model model) {

		Registration reg = registrationService.getRegistrationById(festivalId, user.getId());
		if (reg == null) {
			reg = new Registration();
			reg.setId(new UserFestivalKey(user.getId(), festivalId));
			reg.setMyUser(userRepository.getReferenceById(user.getId()));
			reg.setFestival(festivalRepository.getReferenceById(festivalId));
		}

		reg.setRating(form.getRating());
		reg.setComment(form.getComment());
		reg.setDetailDescription(form.getDetailDescription());

		registrationService.saveOrUpdate(reg);

		model.addAttribute("editedMessage", "Review saved succesfully");

		populateFestivalReviewModel(festivalId, page, size, user, model);

		return "festival-review";
	}

}