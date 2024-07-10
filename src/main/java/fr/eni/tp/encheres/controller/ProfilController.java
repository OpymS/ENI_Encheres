package fr.eni.tp.encheres.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;

import org.springframework.context.MessageSource;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/profil")
@SessionAttributes({ "userSession" })
public class ProfilController {
	private static final Logger profilLogger = LoggerFactory.getLogger(LoginController.class);

	private UserService userService;
	private MessageSource messageSource;

	public ProfilController(UserService userService, MessageSource messageSource) {
		this.userService = userService;
		this.messageSource = messageSource;
	}

	@GetMapping // NOTE : Ajouter vérif sur id valide plus tard
	public String showProfilPage(@SessionAttribute("userSession") User userSession,
			@RequestParam(name = "userId", required = false) int userId, Model model) {
		profilLogger.info("Méthode showProfilPage");
		User userToDisplay = new User();
		userService.fillUserAttributes(userToDisplay, userService.getUserById(userId));
		userToDisplay.setPassword(null); // Pas de stockage de mot de passe

		model.addAttribute("userDisplay", userToDisplay);
		profilLogger.info("affichage profil - userId : " + userToDisplay.getUserId());
		return "profil";
	}

	@GetMapping("/modify")
	public String showModifyProfilPage(Model model, @SessionAttribute("userSession") User userSession) {
		profilLogger.info("Méthode showModifyProfilPage");
		model.addAttribute("user", userSession);
		return "profil-modify";
	}

	@PostMapping("/modify")
	public String modifyUserInfos(@Valid @ModelAttribute("user") User userForm, BindingResult bindingResult,
			@SessionAttribute("userSession") User userSession,
			@RequestParam(name = "updatedPassword", required = false) String updatedPassword,
			@RequestParam(name = "currentPassword", required = false) String currentPassword, Locale locale) {
		profilLogger.info("Méthode modifyUserInfos");
		
		userForm.setUserId(userSession.getUserId());
		userForm.setCredit(userSession.getCredit());
		userForm.setPassword(updatedPassword);
		userSession.setPassword(currentPassword); // On met le mot de passe actuel renseigné dans le formulaire dans
													// l'utilsateur en session pour le récupérer dans le service.
		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(err -> profilLogger.error("id utilisateur connecté : "
					+ userSession.getUserId() + " - erreur sur formulaire modify : " + err));
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
					String errorMessage = messageSource.getMessage(err, null, locale);
					ObjectError error = new ObjectError("globalError", errorMessage);
					bindingResult.addError(error);
					profilLogger.error("id utilisateur connecté : " + userSession.getUserId()
							+ " erreur à la modification du profil : " + err);
				});
				return "profil-modify";
			}
		}
	}

	@GetMapping("/deleteAccount")
	public String deleteUserAccount(@SessionAttribute("userSession") User userSession,
			@RequestParam(name = "userId") int userId, RedirectAttributes redirectAttributes, Locale locale) {
		profilLogger.info("Méthode deleteUserAccount");
		
		// check si c'est bien l'utilisateur connecté qui veut supprimer
		if (userSession.getUserId() != userId) {
			profilLogger.error("Pas le bon utilisateur !");
			return "redirect:/auctions";
		}

		try {
			userService.deleteAccount(userId);

			profilLogger.info("suppression d'un utilisateur - userId : " + userId);

			// Si pas d'erreur, on déconnecte
			return "redirect:/logout";

		} catch (BusinessException e) {
			// NOTE : Une seule erreur s'affiche, même si deux sont présente, à voir ...
			e.getErreurs().forEach(err -> {
				String errorMessage = messageSource.getMessage(err, null, locale);
				redirectAttributes.addFlashAttribute("globalError", errorMessage);
				profilLogger.error("id utilisateur connecté : " + userSession.getUserId()
						+ " - erreur à la suppression du compte : " + err);
			});
			return "redirect:/profil/modify";
		}

	}

}
