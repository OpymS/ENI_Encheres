package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auctions")
public class AuctionController {

	@GetMapping
	public String showAuctionsPage() {
		return "auctions";
	}
	
	@GetMapping("/newArticle")
	public String showArticleCreation() {
		return "article-create";
	}
}
