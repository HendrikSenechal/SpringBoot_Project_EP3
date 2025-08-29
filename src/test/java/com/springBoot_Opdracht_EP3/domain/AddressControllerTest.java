package com.springBoot_Opdracht_EP3.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import domain.AddressController;
import entity.Address;
import services.AddressService;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AddressService addressService;

	private Address addr(long id, String name) {
		Address a = new Address("street", "house", "city", 12345, "country", 1, name);
		try {
			Address.class.getMethod("setId", Long.class).invoke(a, id);
		} catch (Exception ignore) {
		}
		return a;
	}

	@Nested
	class ListAddresses {

		@Test
		@DisplayName("GET /addresses uses defaults and populates paging model")
		void list_defaultParams() throws Exception {
			List<Address> content = List.of(addr(1L, "A"), addr(2L, "B"));
			Page<Address> page = new PageImpl<>(content, PageRequest.of(0, 11), 2);
			given(addressService.getAddresses(null, 0, 11)).willReturn(page);

			mockMvc.perform(get("/addresses")).andExpect(status().isOk()).andExpect(view().name("address-table"))
					.andExpect(model().attribute("addresses", content)).andExpect(model().attribute("currentPage", 0))
					.andExpect(model().attribute("totalPages", 1)).andExpect(model().attribute("pageSize", 11))
					.andExpect(model().attribute("totalElements", 2L)).andExpect(model().attribute("showingFrom", 1))
					.andExpect(model().attribute("showingTo", 2)).andExpect(model().attribute("search", (String) null));

			verify(addressService).getAddresses(null, 0, 11);
		}

		@Test
		@DisplayName("GET /addresses with search + page/size computes start/end correctly")
		void list_withSearchAndPaging() throws Exception {
			String search = "brussels";
			// page=1,size=3,total=8 -> start=(1*3)+1=4, end=4+3-1=6
			List<Address> content = List.of(addr(4L, "C"), addr(5L, "D"), addr(6L, "E"));
			Page<Address> page = new PageImpl<>(content, PageRequest.of(1, 3), 8);
			given(addressService.getAddresses(search, 1, 3)).willReturn(page);

			mockMvc.perform(get("/addresses").param("page", "1").param("size", "3").param("search", search))
					.andExpect(status().isOk()).andExpect(view().name("address-table"))
					.andExpect(model().attribute("currentPage", 1)).andExpect(model().attribute("pageSize", 3))
					.andExpect(model().attribute("totalPages", 3)).andExpect(model().attribute("totalElements", 8L))
					.andExpect(model().attribute("showingFrom", 4)).andExpect(model().attribute("showingTo", 6))
					.andExpect(model().attribute("search", search));

			verify(addressService).getAddresses(search, 1, 3);
		}

		@Test
		@DisplayName("GET /addresses with empty results yields showingFrom=0 and showingTo=0")
		void list_empty() throws Exception {
			Page<Address> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 11), 0);
			given(addressService.getAddresses(null, 0, 11)).willReturn(emptyPage);

			mockMvc.perform(get("/addresses")).andExpect(status().isOk()).andExpect(view().name("address-table"))
					.andExpect(model().attribute("addresses", List.of()))
					.andExpect(model().attribute("totalElements", 0L)).andExpect(model().attribute("showingFrom", 0))
					.andExpect(model().attribute("showingTo", 0));
		}
	}

	@Test
	@DisplayName("GET /addresses/{id} shows details view with model")
	void showAddressDetails() throws Exception {
		Address a = addr(42L, "Z");
		given(addressService.getAddressById(42L)).willReturn(a);

		mockMvc.perform(get("/addresses/42")).andExpect(status().isOk()).andExpect(view().name("address-details"))
				.andExpect(model().attribute("address", a));

		verify(addressService).getAddressById(42L);
	}

	@Test
	@DisplayName("GET /addresses/edit/{id} shows edit view with model")
	void editAddress() throws Exception {
		Address a = addr(7L, "EditMe");
		given(addressService.getAddressById(7L)).willReturn(a);

		mockMvc.perform(get("/addresses/edit/7")).andExpect(status().isOk()).andExpect(view().name("address-edit"))
				.andExpect(model().attribute("address", a));

		verify(addressService).getAddressById(7L);
	}

	@Test
	@DisplayName("GET /address/new prepopulates a new Address and returns edit view")
	void addAddress() throws Exception {
		mockMvc.perform(get("/address/new")).andExpect(status().isOk()).andExpect(view().name("address-edit"))
				.andExpect(model().attributeExists("address"))
				.andExpect(model().attribute("address", org.hamcrest.Matchers.instanceOf(Address.class)));
	}

	@Test
	@DisplayName("POST /updateAddress saves and then returns details view with fresh entity")
	void updateAddress() throws Exception {
		Address submitted = addr(123L, "Updated");
		Address reloaded = addr(123L, "Updated-AfterSave");

		given(addressService.getAddressById(123L)).willReturn(reloaded);

		mockMvc.perform(post("/updateAddress").flashAttr("address", submitted)).andExpect(status().isOk())
				.andExpect(view().name("address-details")).andExpect(model().attribute("address", reloaded));

		ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
		verify(addressService).save(captor.capture());
		assertThat(captor.getValue()).isNotNull();
		verify(addressService).getAddressById(123L);
	}
}
