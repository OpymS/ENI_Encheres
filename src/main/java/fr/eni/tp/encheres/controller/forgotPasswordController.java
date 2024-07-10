package fr.eni.tp.encheres.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.tp.encheres.bll.EmailService;
import fr.eni.tp.encheres.bll.PasswordResetService;
import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.bo.User;

@Controller
public class forgotPasswordController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
    private EmailService emailService;
		
	@GetMapping("/forgot-password")
	public String forgotPasswordForm() {
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email);

        if (user != null) {
            String token = UUID.randomUUID().toString();
            passwordResetService.createPasswordResetToken(token, user.getUserId());

            emailService.sendPasswordResetEmail(user.getEmail(), token);
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
