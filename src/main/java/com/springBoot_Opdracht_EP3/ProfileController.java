package com.springBoot_Opdracht_EP3;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ProfileController {

	@GetMapping("/profile")
	public String showFestivals(Model model) {
		return "profile";
	}
}
