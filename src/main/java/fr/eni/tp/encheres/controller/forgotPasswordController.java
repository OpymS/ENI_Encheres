package fr.eni.tp.encheres.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.tp.encheres.bll.EmailService;
import fr.eni.tp.encheres.bll.PasswordResetService;
import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.User;

@Controller
public class forgotPasswordController {
	private static final Logger forgotPasswordLogger = LoggerFactory.getLogger(forgotPasswordController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
    private EmailService emailService;
		
	@GetMapping("/forgot-password")
	public String forgotPasswordForm() {
		forgotPasswordLogger.info("affichage de la page forgotPassword");
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
        	forgotPasswordLogger.info("id utilisateur demandant un nouveau password : "+user.getUserId());
            String token = UUID.randomUUID().toString();
            passwordResetService.createPasswordResetToken(token, user.getUserId());

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        }

        model.addAttribute("message", "message.password");
        return "forgot-password";
    }
	
	
}
