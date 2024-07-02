package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

	@GetMapping("/")
	public String redirectToAuctions() {
		return "redirect:/auctions";
	}
	
	@GetMapping("/signup")
	public String showSignupPage() {
		return "signup";
	}
}
