package fr.eni.tp.encheres.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({ "userSession" })
public class LoginController {

	private UserService userService;

	public LoginController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@GetMapping("/")
	public String redirectToAuctions() {
		return "redirect:/auctions";
	}

	@GetMapping("/signup")
	public String showSignupPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);

		return "signup";
	}

	@PostMapping("/signup")
	public String processSignup(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(e -> System.out.println(e));
			return "signup";
		} else {
			try {
				userService.createAccount(
						user.getPseudo(),
						user.getName(),
						user.getFirstName(),
						user.getEmail(),
						user.getPhoneNumber(),
						user.getStreet(),
						user.getZipCode(),
						user.getCity(),
						user.getPassword(),
						user.getPasswordConfirm());
				return "redirect:/login";

			} catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					ObjectError error = new ObjectError("globalError", err);
					bindingResult.addError(error);
				});
				return "signup";
			}
		}
	}

	@GetMapping("/session")
	public String fillUserSession(@ModelAttribute("userSession") User userSession, Principal principal) {
		String email;
		User userRecup;
		if(principal != null) {
			email = principal.getName();
			userRecup = userService.getUserByEmail(email);
		} else {
			userRecup = null;
			email = "";
		}
		if (userRecup != null) {
			userService.fillUserAttributes(userSession, userRecup);

			System.out.println(userSession);
		} else {
			userSession.setUserId(0);
			userSession.setEmail(email);
		}

		return "redirect:/auctions";
	}

	@ModelAttribute("userSession")
	public User addUserSession() {
		User userSession = new User();

		return userSession;
	}
}