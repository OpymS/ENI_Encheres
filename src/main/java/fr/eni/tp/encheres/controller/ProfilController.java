package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;

@Controller
@RequestMapping("/profil")
@SessionAttributes({"userSession"})
public class ProfilController {
	
	private UserService userService;
	
	public ProfilController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public String showProfilPage(@ModelAttribute("userSession") User userSession) {
		return "profil";
	}
	
	@GetMapping("/modify")
	public String showModifyProfilPage() {
		return "profil-modify";
	}
	
	@PostMapping("/modify")
	public String modifyUserInfos(@ModelAttribute("userForm") User userForm, @SessionAttribute("userSession") User userSession) {
		
		userForm.setUserId(userSession.getUserId());
		userForm.setCredit(userSession.getCredit());
		System.out.println("userFrom :" + userForm);
		
		
		userService.updateProfile(userForm, userSession);
		User userWithUpdates = userService.viewUserProfile(userForm.getUserId());
		
		System.out.println("userWithUpdates : " + userWithUpdates);
		
		userService.fillUserAttributes(userSession, userWithUpdates);
		
		return "redirect:/profil";
	}
}
