package com.springBoot_Opdracht_EP3;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import security.MyUserDetailsService;
import services.AddressService;
import services.AddressServiceImpl;
import services.CategoryService;
import services.CategoryServiceImpl;
import services.FestivalService;
import services.FestivalServiceImpl;
import services.MyUserService;
import services.MyUserServiceImpl;
import services.RegistrationService;
import services.RegistrationServiceImpl;
import services.VendorService;
import services.VendorServiceImpl;

@SpringBootApplication(scanBasePackages = { "com.springBoot_Opdracht_EP3", "com.springBoot_Opdracht_EP3", "repository",
		"domain", "security", "rest" })
@EntityScan("entity")
@EnableJpaRepositories(basePackages = "repository")
public class SpringBootProjectEp3Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootProjectEp3Application.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/festivals");
		registry.addViewController("/403").setViewName("403");
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

	@Bean
	MyUserService myUserService() {
		return new MyUserServiceImpl();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new MyUserDetailsService();
	}

	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		registry.addInterceptor(localeChangeInterceptor);
	}
}