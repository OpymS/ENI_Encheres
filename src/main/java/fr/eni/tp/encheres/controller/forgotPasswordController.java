package fr.eni.tp.encheres.controller;


import java.util.Date;

import java.util.Locale;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.tp.encheres.bll.EmailService;
import fr.eni.tp.encheres.bll.PasswordResetService;
import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

@Controller
public class forgotPasswordController {
	private static final Logger forgotPasswordLogger = LoggerFactory.getLogger(forgotPasswordController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private MessageSource messageSource;
		
	@GetMapping("/forgot-password")
	public String forgotPasswordForm() {
		forgotPasswordLogger.info("Méthode forgotPassword");
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes, Locale locale) {
		forgotPasswordLogger.info("Méthode handleForgotPassword");
		
        User user = userService.getUserByEmail(email);
        if (user != null) {
        	forgotPasswordLogger.info("id utilisateur demandant un nouveau password : "+user.getUserId());
            String token = UUID.randomUUID().toString();
            passwordResetService.createPasswordResetToken(token, user.getUserId());

            try {
				emailService.sendPasswordResetEmail(user.getEmail(), token);
			} catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					String errorMessage = messageSource.getMessage(err, null, locale);
					redirectAttributes.addFlashAttribute("globalError", errorMessage);
					forgotPasswordLogger.error("id utilisateur connecté : " + user.getUserId() +" " + err);
				});
				
			}
        }

        model.addAttribute("message", "message.password");
        return "forgot-password";
    }
	
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = passwordResetService.findByToken(token);
        if (resetToken == null) {
            model.addAttribute("error", "Le token est invalide.");
            return "error";
        }
        if (resetToken.getExpiryDate().before(new Date())) {
            model.addAttribute("error", "Le token a expiré.");
            return "error";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      Model model) {
        PasswordResetToken resetToken = passwordResetService.findByToken(token);
        if (resetToken == null) {
            model.addAttribute("error", "Le token est invalide.");
            return "error";
        }
        if (resetToken.getExpiryDate().before(new Date())) {
            model.addAttribute("error", "Le token a expiré.");
            return "error";
        }

        try {
            userService.updatePassword(resetToken.getUserId(), password);
            passwordResetService.deleteToken(resetToken);
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur s'est produite lors de la réinitialisation du mot de passe.");
            return "error";
        }

        model.addAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
        return "login";
    }
}
