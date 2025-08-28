package domain;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import lombok.extern.slf4j.Slf4j;
import security.CustomUserDetails;
import services.AddressService;
import services.CategoryService;
import services.FestivalService;
import services.RegistrationService;
import services.VendorService;
import utils.FestivalCodeGenerator;

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
	public String listFestivals(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "11") int size,
			@RequestParam(required = false) String search, @RequestParam(required = false) Long categoryId,
			@RequestParam(defaultValue = "ALL") String status, Model model, Principal principal) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("start").ascending());
		Page<Festival> festivalPage = festivalService.getFestivals(pageable, search, categoryId, status);

		model.addAttribute("username", principal.getName());
		model.addAttribute("festivals", festivalPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPages", festivalPage.getTotalPages());
		model.addAttribute("totalItems", festivalPage.getTotalElements());

		model.addAttribute("search", search);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("status", status);
		model.addAttribute("categories", categoryService.getAllCategories());

		return "festival-table";
	}

	@GetMapping("/festivals/{festivalId}")
	public String showFestivalDetail(@PathVariable("festivalId") Long festivalId,
			@AuthenticationPrincipal CustomUserDetails user, Model model) {
		populateFestivalDetailModel(festivalId, user, model);
		return "festival-details";
	}

	@PostMapping("/festivals/{festivalId}")
	public String saveFestivalTickets(@PathVariable("festivalId") Long festivalId,
			@RequestParam("orderedTickets") int tickets, @AuthenticationPrincipal CustomUserDetails user, Model model) {
		registrationService.buyTickets(user.getId(), festivalId, tickets);
		populateFestivalDetailModel(festivalId, user, model);
		return "festival-details";
	}

	@PostMapping("/festivals/{id}/delete")
	public String removeRegistration(@PathVariable("id") Long festivalId,
			@AuthenticationPrincipal CustomUserDetails user, Model model) {
		registrationService.deleteById(festivalId, user.getId());
		populateFestivalDetailModel(festivalId, user, model);
		return "festival-details";
	}

	private void populateFestivalDetailModel(@PathVariable("festivalId") Long festivalId,
			@AuthenticationPrincipal CustomUserDetails user, Model model) {
		model.addAttribute("festival", festivalService.getFestivalById(festivalId));
		model.addAttribute("averageRating", registrationService.getAverageRatingForFestival(festivalId));
		model.addAttribute("reservedTickets", registrationService.getTicketsByFestival(festivalId));
		model.addAttribute("userTickets", registrationService.getTicketsByFestivalAndUser(festivalId, user.getId()));
		List<Registration> top10Reviews = registrationService.getTop10Reviews(festivalId);
		model.addAttribute("registrations", top10Reviews);
	}

	@GetMapping("/festivals/edit/{id}")
	public String editFestival(@PathVariable("id") Long id, Model model) {
		populateNewOrEditModel(model, festivalService.getFestivalById(id));
		return "festival-edit";
	}

	@GetMapping("/festivals/new")
	public String newFestival(Model model) {
		int[] codes = FestivalCodeGenerator.generateValidCodes();
		populateNewOrEditModel(model, new Festival("", "", codes[0], codes[1], new BigDecimal(10.50), 0));
		return "festival-edit";
	}

	public void populateNewOrEditModel(Model model, Festival festival) {
		model.addAttribute("categories", categoryService.getAllCategories());
		model.addAttribute("addresses", addressService.getAllAddresses());
		model.addAttribute("vendors", vendorService.getAllVendors());
		model.addAttribute("festival", festival);
	}

	@PostMapping("/updateFestival")
	public String saveOrUpdateFestival(@ModelAttribute Festival festival,
			@RequestParam(value = "vendorIds", required = false) List<Long> vendorIds,
			@AuthenticationPrincipal CustomUserDetails user, Model model) {
		festivalService.save(festival, vendorIds);
		populateFestivalDetailModel(festival.getId(), user, model);
		return "festival-details";
	}

	// REVIEW

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
		reg.setRating(form.getRating());
		reg.setComment(form.getComment());
		reg.setDetailDescription(form.getDetailDescription());
		registrationService.saveOrUpdate(reg);

		model.addAttribute("editedMessage", "Review saved succesfully");
		populateFestivalReviewModel(festivalId, page, size, user, model);
		return "festival-review";
	}

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

}