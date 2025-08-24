package com.springBoot_Opdracht_EP3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import domain.AddressService;
import domain.AddressServiceImpl;
import domain.CategoryService;
import domain.CategoryServiceImpl;
import domain.FestivalService;
import domain.FestivalServiceImpl;
import domain.VendorService;
import domain.VendorServiceImpl;
import populate.PopulateDB;
import repository.AddressDaoJpa;
import repository.CategoryDaoJpa;
import repository.VendorDaoJpa;

@SpringBootApplication
public class SpringBootProjectEp3Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootProjectEp3Application.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/login");
	}

	@Bean
	FestivalService festivalService() {
		return new FestivalServiceImpl();
	}

	@Bean
	VendorService vendorTableService() {
		return new VendorServiceImpl();
	}

	@Bean
	AddressService addressService() {
		return new AddressServiceImpl();
	}

	@Bean
	CategoryService categoryService() {
		return new CategoryServiceImpl();
	}

	@Bean
	VendorDaoJpa vendorDaoJpa() {
		return new VendorDaoJpa();
	}

	@Bean
	AddressDaoJpa addressDaoJpa() {
		return new AddressDaoJpa();
	}

	@Bean
	CategoryDaoJpa categoryDaoJpa() {
		return new CategoryDaoJpa();
	}

	@Bean
	public CommandLineRunner runOnStartup() {
		return args -> {
			new PopulateDB().runDAO();
		};
	}
}