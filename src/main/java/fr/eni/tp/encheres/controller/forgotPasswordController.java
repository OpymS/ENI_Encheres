package fr.eni.tp.encheres.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import fr.eni.tp.encheres.bll.PasswordResetService;

@Controller
public class forgotPasswordController {
	
	@Autowired
	private PasswordResetService emailService;
	
	@GetMapping("/forgot-password")
	public String forgotPasswordForm() {
		return "forgot-password";
	}

	
	
}
