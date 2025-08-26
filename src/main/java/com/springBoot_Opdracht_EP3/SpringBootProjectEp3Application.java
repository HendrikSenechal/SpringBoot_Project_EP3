package com.springBoot_Opdracht_EP3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import services.AddressService;
import services.AddressServiceImpl;
import services.CategoryService;
import services.CategoryServiceImpl;
import services.FestivalService;
import services.FestivalServiceImpl;
import services.RegistrationService;
import services.RegistrationServiceImpl;
import services.VendorService;
import services.VendorServiceImpl;

@SpringBootApplication(scanBasePackages = { "com.springBoot_Opdracht_EP3", "repository" })
@EnableJpaRepositories("repository")
@EntityScan("entity")
public class SpringBootProjectEp3Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootProjectEp3Application.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/festivals");
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
	RegistrationService registrationService() {
		return new RegistrationServiceImpl();
	}
}