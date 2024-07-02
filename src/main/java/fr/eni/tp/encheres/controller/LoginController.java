package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LoginController {

	
	@GetMapping
	public String redirectToAuctions() {
		return "redirect:/auctions";
	}
	
	@GetMapping("/signup")
	public String showSignupPage() {
		return "signup";
	}
}
