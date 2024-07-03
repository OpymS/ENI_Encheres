package fr.eni.tp.encheres.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;


@Controller
@SessionAttributes({"userSession"})
public class LoginController {
	
	private UserService userService;
	
	public LoginController(UserService userService){
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
	
	//Récupère les infos fournies dans le formulaire d'inscription:
	// Ràf :
	//	- vérifier que le pseudo n'est pas déjà dans la base de données
	//	- vérifier que l'email n'est pas déjà dans la base de données
	//	- vérification générale/validation du format des données entrées (ex : password == passwordConfirm)
	// 	- ?? gestion du mot de passe avec bcrypt pour écriture en base de données et stockage
	//Si tout ok, alors ajout à la base de données.
	@PostMapping("/signup")
	public String validateSignup(@ModelAttribute("user") User user) {
		System.err.println(user);
		System.out.println(user.getPassword());
		System.out.println(user.getPasswordConfirm());
		return "redirect:/auctions";
	}

	@GetMapping("/session")
	public String fillUserSession(@ModelAttribute("userSession") User userSession, Principal principal) {
		String email = principal.getName();
		User userRecup = userService.getUserByEmail(email);
		
		if(userRecup != null) {
			userSession.setUserId(userRecup.getUserId());
			userSession.setPseudo(userRecup.getPseudo());
			userSession.setName(userRecup.getName());
			userSession.setFirstName(userRecup.getFirstName());
			userSession.setEmail(userRecup.getEmail());
			userSession.setPhoneNumber(userRecup.getPhoneNumber());
			userSession.setStreet(userRecup.getStreet());
			userSession.setZipCode(userRecup.getZipCode());
			userSession.setCity(userRecup.getCity());
			//password ?
			userSession.setAdmin(userRecup.isAdmin());
			System.out.println(userSession);
		}else{
			userSession.setUserId(0);
			userSession.setPseudo(null);
			userSession.setName(null);
			userSession.setFirstName(null);
			userSession.setEmail(email);
			userSession.setPhoneNumber(null);
			userSession.setStreet(null);
			userSession.setZipCode(null);
			userSession.setCity(null);
			//password ?
			userSession.setAdmin(false);
		}
		
		return "redirect:/auctions";
	}
	
	
	@ModelAttribute("userSession")
	public User addUserSession() {
		User userSession = new User();
		
		return userSession;
	}
}
