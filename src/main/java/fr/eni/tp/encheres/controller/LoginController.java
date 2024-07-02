package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import fr.eni.tp.encheres.bo.User;


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
}
