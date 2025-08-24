package com.springBoot_Opdracht_EP3;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String showVendors(Model model) {
		return "login";
	}
	/*
	 * @GetMapping("/login") private void handleLoginButtonClick(String email,
	 * String password, Model model) { String currentPassword = passwordVisible ?
	 * passwordText.getText() : passwordHidden.getText(); try { User user =
	 * loginDomainController.login(email.getText().trim(), currentPassword);
	 * 
	 * if (user.getRole() == Role.SUPERVISOR) { SiteDomainController
	 * siteDomainController = new SiteDomainController();
	 * user.setSite(siteDomainController.getSiteBySupervisor(loginDomainController.
	 * getUser())); }
	 * 
	 * nextWindow(user); userDomainController.setLoggedInUser(user);
	 * 
	 * }
	 */
}
