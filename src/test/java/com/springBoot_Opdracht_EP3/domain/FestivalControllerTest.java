package com.springBoot_Opdracht_EP3.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.springBoot_Opdracht_EP3.SpringBootProjectEp3Application;

import domain.FestivalController;
import entity.Address;
import entity.Category;
import entity.Festival;
import security.CustomUserDetails;
import services.AddressService;
import services.CategoryService;
import services.FestivalService;
import services.RegistrationService;
import services.VendorService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FestivalController.class)
@ContextConfiguration(classes = SpringBootProjectEp3Application.class)
class FestivalControllerTest {

	@Autowired
	private MockMvc mvc;

	// Collaborators mocked so the MVC slice focuses on the controller
	@MockitoBean
	private CategoryService categoryService;
	@MockitoBean
	private AddressService addressService;
	@MockitoBean
	private VendorService vendorService;
	@MockitoBean
	private FestivalService festivalService;
	@MockitoBean
	private RegistrationService registrationService;
	@MockitoBean
	private UserDetailsService userDetailsService;

	private RequestPostProcessor authWith(CustomUserDetails cud) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(cud, cud.getPassword(),
				cud.getAuthorities());
		return SecurityMockMvcRequestPostProcessors.authentication(auth);
	}

	private CustomUserDetails demoUser() {
		return new CustomUserDetails(42L, "alice", "pw", "Alice A.", List.of(new SimpleGrantedAuthority("ROLE_USER")));
	}

	private Festival demoFestival(Long id) {
		Festival f = new Festival();
		f.setId(id);
		f.setName("RockFest");
		f.setDescription("Great music");
		f.setStart(LocalDateTime.now().plusDays(5));
		f.setEnd(LocalDateTime.now().plusDays(7));
		f.setPrice(new BigDecimal("12.50"));

		// category for: festival.category.name
		Category cat = new Category();
		cat.setName("Music");
		cat.setDescription("All music festivals");
		f.setCategory(cat);

		// address for: festival.address.name
		Address addr = new Address();
		addr.setName("Main Arena");
		addr.setCountry("BE");
		addr.setPlace("Antwerpen");
		addr.setPostcode(2000);
		addr.setStreet("Groenplaats");
		addr.setNumber(1);
		addr.setAddition("");
		f.setAddress(addr);

		return f;
	}

	@Test
	@DisplayName("GET /festivals uses defaults and renders festival-table with pagination + filters")
	void listFestivals_default() throws Exception {
		// Given
		Page<Festival> page = new PageImpl<>(List.of(demoFestival(1L)), PageRequest.of(0, 11), 1);
		when(festivalService.getFestivals(any(Pageable.class), isNull(), isNull(), eq("ALL"))).thenReturn(page);
		when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

		// When/Then
		mvc.perform(get("/festivals").with(authWith(demoUser()))).andExpect(status().isOk())
				.andExpect(view().name("festival-table"))
				.andExpect(model().attributeExists("festivals", "currentPage", "pageSize", "totalPages", "totalItems",
						"search", "categoryId", "status", "categories"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 11))
				.andExpect(model().attribute("totalPages", 1)).andExpect(model().attribute("totalItems", 1L))
				.andExpect(model().attribute("status", "ALL"));

		// Verify pageable (sorted by start ascending & default page size)
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(festivalService).getFestivals(pageableCaptor.capture(), isNull(), isNull(), eq("ALL"));
		Pageable used = pageableCaptor.getValue();
		assert used.getPageSize() == 11 : "Expected default page size 11";
		assert used.getSort().getOrderFor("start") != null : "Expected sort by 'start'";
		assert used.getSort().getOrderFor("start").isAscending() : "Expected ascending sort by 'start'";
	}

	@Test
	@DisplayName("GET /festivals/{id} populates detail model and returns festival-details")
	void showFestivalDetail() throws Exception {
		long festId = 10L;
		CustomUserDetails cud = demoUser();
		when(festivalService.getFestivalById(festId)).thenReturn(demoFestival(festId));
		when(registrationService.getAverageRatingForFestival(festId)).thenReturn(4.2);
		when(registrationService.getTicketsByFestival(festId)).thenReturn(120);
		when(registrationService.getTicketsByFestivalAndUser(festId, cud.getId())).thenReturn(3);
		when(registrationService.getTop10Reviews(festId)).thenReturn(Collections.emptyList());

		mvc.perform(get("/festivals/{festivalId}", festId).with(authWith(cud))).andExpect(status().isOk())
				.andExpect(view().name("festival-details"))
				.andExpect(model().attributeExists("festival", "averageRating", "reservedTickets", "userTickets",
						"registrations"))
				.andExpect(model().attribute("averageRating", 4.2)).andExpect(model().attribute("reservedTickets", 120))
				.andExpect(model().attribute("userTickets", 3));

		verify(festivalService).getFestivalById(festId);
		verify(registrationService).getTop10Reviews(festId);
	}
}