package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class LanguageController {

	@GetMapping("/changeLanguage")
	public RedirectView changeLanguage(HttpServletRequest request, @RequestParam("language") String language) {
	    String referer = request.getHeader("Referer");
	    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(referer)
	            .replaceQueryParam("language", language);

	    RedirectView redirectView = new RedirectView();
	    redirectView.setUrl(uriBuilder.toUriString());
	    return redirectView;
	}
}
