package domain;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DashboardController {

	@GetMapping("/dashboard")
	public String showFestivals(Model model) {
		return "dashboard"; // name of the HTML file (festival-table.html)
	}
}
