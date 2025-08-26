package com.springBoot_Opdracht_EP3;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import security.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("fullName")
	public String populateColors(Authentication authentication) {
		if (authentication == null)
			return "";
		else {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			return userDetails.getFullName();
		}
	}
}