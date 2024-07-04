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
import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;

@Controller
@RequestMapping("/bid")
@SessionAttributes({"userSession"})
public class BidController {
	
	private AuctionService auctionService;
	private UserService userService;
	
	public BidController(AuctionService auctionService, UserService userService) {
		this.auctionService = auctionService;
		this.userService =userService;
	}

	
	@GetMapping
	public String showArticleToBidOn(@RequestParam(name="articleId", required=true) int articleId,
									@SessionAttribute("userSession") User userSession,
									Model model) {
		Article articleToDisplay = auctionService.findArticleById(articleId);
		//System.out.println(articleToDisplay);
		
		
		
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
		
		if(userSession.getCredit() < bidOffer) { //Si crédit de l'utilisateur inférieur à la mise alors PAS OK
			System.err.println("Pas assez de sousous !");
			String redirectUrl = "redirect:/bid?articleId=" + articleId;
			return redirectUrl;
		}else { // Donc crédit OK
			Article article = auctionService.findArticleById(articleId);
			User currentBuyer = article.getCurrentBuyer(); //Stocke de l'utilisateur courant
			
			Auction newAuction = new Auction();
			newAuction.setAuctionDate(LocalDateTime.now());
			newAuction.setUser(userSession);
			newAuction.setArticle(article);
			newAuction.setBidAmount(bidOffer);
			
			boolean isBidOk = auctionService.newAuction(newAuction); // La méthode retourne true si l'enchère s'est faite, false sinon.
			
			if(!isBidOk) { // Alors erreur de temps ou de montant pas supérieur au précédent
				String redirectUrl = "redirect:/bid?articleId=" + articleId;
				return redirectUrl;
			}else { // Alors tout est bon et on peut rembourser et débiter les utilisateurs concernés
				currentBuyer.setCredit(currentBuyer.getCredit()+article.getCurrentPrice());
				userSession.setCredit(userSession.getCredit()-bidOffer);
				
				userService.updateUserCredit(currentBuyer);
				userService.updateUserCredit(userSession);
				
				
				String redirectUrl = "redirect:/bid?articleId=" + articleId;
				return redirectUrl;
			}
			
		}
		
		
	}
	
}
