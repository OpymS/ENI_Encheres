package fr.eni.tp.encheres.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;

@Controller
@RequestMapping("/bid")
@SessionAttributes({"userSession"})
public class BidController {
	
	private AuctionService auctionService;
	
	public BidController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	
	@GetMapping
	public String showArticleToBidOn(@RequestParam(name="articleId", required=true) int articleId,
									@SessionAttribute("userSession") User userSession,
									Model model) {
		Article articleToDisplay = auctionService.findArticleById(articleId);
		System.out.println(articleToDisplay);
		
		
		
		// Check si on peut enchérir
		// càd la date actuelle est entre la date de début et la date de fin de l'enchère.
		boolean isBidPossible = (LocalDateTime.now().isAfter(articleToDisplay.getAuctionStartDate()) 
								&& LocalDateTime.now().isBefore(articleToDisplay.getAuctionEndDate()));
		
		// Changement de l'enchère/article possible (annulation)
		// si la date actuelle est avant le début ou pendant l'enchère
		//boolean isBeforeStart = LocalDateTime.now().isBefore(articleToDisplay.getAuctionStartDate());
		//boolean isChangePossible = isBeforeStart || isBidPossible;
		//model.addAttribute("isChangePossible", isChangePossible);
		
		model.addAttribute("articleDisplay", articleToDisplay);
		model.addAttribute("userSession", userSession);
		model.addAttribute("isBidPossible", isBidPossible);
		
		
		return "bid-article-detail";
	}
	
	@PostMapping
	public String createBidOnArticle(@RequestParam(name="articleId", required=true) int articleId,
									@RequestParam(name="bidOffer", required=true) int bidOffer,
									@SessionAttribute("userSession") User userSession) {
		Auction newAuction = new Auction();
		Article article = auctionService.findArticleById(articleId);
		
		newAuction.setAuctionDate(LocalDateTime.now());
		newAuction.setUser(userSession);
		newAuction.setArticle(article);
		newAuction.setBidAmount(bidOffer);
		
		auctionService.newAuction(newAuction);
		
		
		String redirectUrl = "redirect:/bid?articleId=" + articleId;
		return redirectUrl;
	}
	
}
