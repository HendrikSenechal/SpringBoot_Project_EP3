package com.springBoot_Opdracht_EP3;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

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

	@ControllerAdvice
	public class SecurityExceptionHandler {
		@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
		public String handleAccessDenied() {
			return "forward:/403";
		}

		@ExceptionHandler(Exception.class)
		@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
		public String handleAny() {
			return "forward:/error"; // or your generic page
		}
	}

}