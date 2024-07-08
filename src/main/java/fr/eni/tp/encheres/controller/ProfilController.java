package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/profil")
@SessionAttributes({ "userSession" })
public class ProfilController {

	private UserService userService;

	public ProfilController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping // NOTE : Ajouter vérif sur id valide plus tard
	public String showProfilPage(@SessionAttribute("userSession") User userSession,
			@RequestParam(name = "userId", required = false) int userId, Model model) {
		User userToDisplay = new User();
		userService.fillUserAttributes(userToDisplay, userService.getUserById(userId));
		userToDisplay.setPassword(null); // Pas de stockage de mot de passe

		model.addAttribute("userDisplay", userToDisplay);

		return "profil";
	}

	@GetMapping("/modify")
	public String showModifyProfilPage(Model model, @SessionAttribute("userSession") User userSession) {
		model.addAttribute("user", userSession);
		return "profil-modify";
	}

	@PostMapping("/modify")
	public String modifyUserInfos(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult,
			@SessionAttribute("userSession") User userSession,
			@RequestParam(name = "updatedPassword", required = false) String updatedPassword,
			@RequestParam(name = "currentPassword", required = false) String currentPassword) {

		userForm.setUserId(userSession.getUserId());
		userForm.setCredit(userSession.getCredit());
		userForm.setPassword(updatedPassword);
		userSession.setPassword(currentPassword); // On met le mot de passe actuel renseigné dans le formulaire dans
													// l'utilsateur en session pour le récupérer dans le service.
		if (bindingResult.hasErrors()) {
			return "profil-modify";
		} else {
			try {
				userService.updateProfile(userForm, userSession);
				User userWithUpdates = userService.viewUserProfile(userForm.getUserId());
				userService.fillUserAttributes(userSession, userWithUpdates);
				userSession.setPassword(null); // On ne stocke pas le mot de passe de l'utilisateur en session
				String redirectUrl = "redirect:/profil?userId=" + userSession.getUserId();
				return redirectUrl;
			} catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					ObjectError error = new ObjectError("globalError", err);
					bindingResult.addError(error);
				});
				return "profil-modify";
			}
		}
	}
	
	
	@GetMapping("/deleteAccount")
	public String deleteUserAccount(@SessionAttribute("userSession") User userSession, @RequestParam(name="userId") int userId) {
		
		//check si c'est bien l'utilisateur connecté qui veut supprimer
		if(userSession.getUserId()!=userId) {
			System.err.println("Pas le bon utilisateur !");
			return "redirect:/auctions";
		}
		
		
		userService.deleteAccount(userId);
		
	
		
		return "redirect:/auctions";
	}
	
	
}
