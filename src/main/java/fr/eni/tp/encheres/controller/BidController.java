package fr.eni.tp.encheres.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bll.UserService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

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
	
		// Check si on peut enchérir
		// càd la date actuelle est entre la date de début et la date de fin de l'enchère.
		boolean isBidPossible = (LocalDateTime.now().isAfter(articleToDisplay.getAuctionStartDate()) 
								&& LocalDateTime.now().isBefore(articleToDisplay.getAuctionEndDate()));
		
		// Changement de l'enchère/article possible (modification/annulation)
		// si la date actuelle est avant le début ou pendant l'enchère
		boolean isBeforeStart = LocalDateTime.now().isBefore(articleToDisplay.getAuctionStartDate());
		boolean isChangePossible = isBeforeStart || isBidPossible;
		
		
		model.addAttribute("articleDisplay", articleToDisplay);
		model.addAttribute("userSession", userSession);
		model.addAttribute("isBidPossible", isBidPossible);
		model.addAttribute("isChangePossible", isChangePossible);
		
		
		// Ajout de la date au bon format !
		String dateDisplayFormat = "dd/MM/yyyy - HH:mm";
		DateTimeFormatter dtFormater = DateTimeFormatter.ofPattern(dateDisplayFormat);
		
		String startDateDisplay = dtFormater.format(articleToDisplay.getAuctionStartDate());
		String endDateDisplay = dtFormater.format(articleToDisplay.getAuctionEndDate());
		
		model.addAttribute("startDateDisplay", startDateDisplay);
		model.addAttribute("endDateDisplay", endDateDisplay);
		
		return "bid-article-detail";
	}
	
	
	@PostMapping
	public String createBidOnArticle(@RequestParam(name="articleId", required=true) int articleId,
									@RequestParam(name="bidOffer", required=true) int bidOffer,
									@Valid @SessionAttribute("userSession") User userSession, RedirectAttributes redirectAttributes) {
		
		String redirectUrl = "redirect:/bid?articleId=" + articleId;
		
		try {
			auctionService.newAuction(articleId, bidOffer, userSession);
			
		} catch (BusinessException e) {
			e.getErreurs().forEach(err -> redirectAttributes.addFlashAttribute("globalError", err));
		}
		return redirectUrl;
		
			
	}
}
