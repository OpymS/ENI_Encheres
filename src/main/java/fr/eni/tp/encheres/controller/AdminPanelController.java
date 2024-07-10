package fr.eni.tp.encheres.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

@Controller
@RequestMapping("/admin")
@SessionAttributes({"userSession"})
public class AdminPanelController {
	private static final Logger adminPannelLogger = LoggerFactory.getLogger(AdminPanelController.class);
	
	private UserService userService;
	
	public AdminPanelController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public String showAdminPanel(Model model) {
		adminPannelLogger.info("Méthode showAdminPanel");
		List<User> usersList = userService.getAllUsers();
		
		model.addAttribute("users", usersList);
		
		return "admin-panel";
	}

	@GetMapping("/deleteAccount")
	public String deleteUserAccount(@RequestParam(name="userId", required=false) int userId,
			@SessionAttribute("userSession") User userSession,
			RedirectAttributes redirectAttributes){
		adminPannelLogger.info("Méthode deleteUserAccount");
		
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
			e.getErreurs().forEach(err -> redirectAttributes.addFlashAttribute("globalError", err));
		}
		
		return "redirect:/admin";
	}
	
	@GetMapping("/desacAccount")
	public String desactivateUserAccount(@RequestParam(name="userId", required=false) int userId,
			@SessionAttribute("userSession") User userSession,
			RedirectAttributes redirectAttributes) {
		adminPannelLogger.info("Méthode desactivateUserAccount");
		
		//On vérifie quand même si l'utilisateur qui appelle cet url est administrateur
		if(!userSession.isAdmin()) {
			System.err.println("Vous n'êtes pas admin ! Dégagez");
			return "redirect:/auctions";
		}
		
		try {
			userService.desactivateAccount(userId);
		} catch (BusinessException e) {
			System.err.println(e);
			//Modifier les erreurs créer par les checks
			e.getErreurs().forEach(err -> redirectAttributes.addFlashAttribute("globalError", err));
		}
		
		return "redirect:/admin";
	}
	
	@GetMapping("/reactivateAccount")
	public String reactivateUserAccount(@RequestParam(name="userId", required=false) int userId,
			@SessionAttribute("userSession") User userSession,
			RedirectAttributes redirectAttributes) {
		adminPannelLogger.info("Méthode reactivateUserAccount");
		//On vérifie quand même si l'utilisateur qui appelle cet url est administrateur
		if(!userSession.isAdmin()) {
			System.err.println("Vous n'êtes pas admin ! Dégagez");
			return "redirect:/auctions";
		}
		
		try {
			userService.reactivateAccount(userId);
		} catch (BusinessException e) {
			System.err.println(e);
			e.getErreurs().forEach(err -> redirectAttributes.addFlashAttribute("globalError", err));
		}
		
		return "redirect:/admin";
	}
	
	
	
}
