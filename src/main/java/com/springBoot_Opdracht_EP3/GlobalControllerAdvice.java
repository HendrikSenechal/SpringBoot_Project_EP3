package com.springBoot_Opdracht_EP3;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("username")
	public String populateColors(Authentication authentication) {
		return authentication == null ? "" : authentication.getName();
	}

}