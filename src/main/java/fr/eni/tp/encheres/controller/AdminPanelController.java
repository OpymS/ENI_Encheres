package fr.eni.tp.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

@Controller
@RequestMapping("/admin")
@SessionAttributes({"userSession"})
public class AdminPanelController {
	
	private UserService userService;
	
	public AdminPanelController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public String showAdminPanel(Model model) {
		List<User> usersList = userService.getAllUsers();
		
		model.addAttribute("users", usersList);
		
		return "admin-panel";
	}

	@GetMapping("/deleteAccount")
	public String deleteUserAccount(@RequestParam(name="userId", required=false) int userId, @SessionAttribute("userSession") User userSession) {
		
		System.err.println("userId : "+userId);
		System.out.println(userSession);
		
		//On vérifie quand même si l'utilisateur qui appelle cet url est administrateur
		if(!userSession.isAdmin()) {
			System.err.println("Vous n'êtes pas admin ! Dégagez");
			return "redirect:/auctions";
		}
		
		try {
			userService.deleteAccount(userId);
		} catch (BusinessException e) {
			System.err.println(e);
		}
		
		
		
		
		return "redirect:/admin";
	}
	
	@GetMapping("/desacAccount")
	public String desactivateUserAccount(@RequestParam(name="userId", required=false) int userId, @SessionAttribute("userSession") User userSession) {
		
		System.err.println("userId : "+userId);
		System.out.println(userSession);
		
		//On vérifie quand même si l'utilisateur qui appelle cet url est administrateur
		if(!userSession.isAdmin()) {
			System.err.println("Vous n'êtes pas admin ! Dégagez");
			return "redirect:/auctions";
		}
		
		try {
			userService.desactivateAccount(userId);
		} catch (BusinessException e) {
			System.err.println(e);
		}
		
		
		
		
		return "redirect:/admin";
	}
	
	
	
}
