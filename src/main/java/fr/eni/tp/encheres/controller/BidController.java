package fr.eni.tp.encheres.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
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
import fr.eni.tp.encheres.bo.ArticleState;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

@Controller
@RequestMapping("/bid")
@SessionAttributes({ "userSession" })
public class BidController {

	private AuctionService auctionService;
	private UserService userService;
	private MessageSource messageSource;

	public BidController(AuctionService auctionService, UserService userService, MessageSource messageSource) {
		this.auctionService = auctionService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	@GetMapping
	public String showArticleToBidOn(@RequestParam(name = "articleId", required = true) int articleId,
			@SessionAttribute("userSession") User userSession, Model model) {
		Article articleToDisplay = auctionService.findArticleById(articleId);
		System.out.println(articleToDisplay);

		// Gestion de l'affichae conditionnel sur la page
		boolean isBidPossible = articleToDisplay.getState().equals(ArticleState.STARTED);
		boolean isBeforeStart = articleToDisplay.getState().equals(ArticleState.NOT_STARTED);
		boolean isChangePossible = isBeforeStart || isBidPossible;
		boolean isAuctionCanceled = articleToDisplay.getState().equals(ArticleState.CANCELED);
		boolean isAuctionFinished = articleToDisplay.getState().equals(ArticleState.FINISHED)
				|| articleToDisplay.getState().equals(ArticleState.RETRIEVED);

		model.addAttribute("articleDisplay", articleToDisplay);
		model.addAttribute("userSession", userSession);
		model.addAttribute("isChangePossible", isChangePossible);
		model.addAttribute("isBidPossible", isBidPossible);
		model.addAttribute("isBeforeStart", isBeforeStart);
		model.addAttribute("isAuctionFinished", isAuctionFinished);
		model.addAttribute("isAuctionCanceled", isAuctionCanceled);

		// Ajout de la date au bon format !
		String dateDisplayFormat = "dd/MM/yyyy - HH:mm";
		DateTimeFormatter dtFormater = DateTimeFormatter.ofPattern(dateDisplayFormat);

		String startDateDisplay = dtFormater.format(articleToDisplay.getAuctionStartDate());
		String endDateDisplay = dtFormater.format(articleToDisplay.getAuctionEndDate());

		model.addAttribute("startDateDisplay", startDateDisplay);
		model.addAttribute("endDateDisplay", endDateDisplay);

		// Récup des enchères sur cet article et tri
		List<Auction> bidsList = auctionService.findAllAuctions(articleId);
		
		bidsList.sort((a,b)->b.getBidAmount()-a.getBidAmount());
		
		model.addAttribute("bids", bidsList);
		model.addAttribute("isThereBids", bidsList.size()!=0);		

		return "bid-article-detail";
	}

	@PostMapping
	public String createBidOnArticle(@RequestParam(name = "articleId", required = true) int articleId,
			@RequestParam(name = "bidOffer", required = true) int bidOffer,
			@SessionAttribute("userSession") User userSession, RedirectAttributes redirectAttributes, Locale locale) {

		String redirectUrl = "redirect:/bid?articleId=" + articleId;

		try {
			//Si utilisateur désactivé, on empèche l'enchère
			if(!userSession.isActivated()) {
				BusinessException be = new BusinessException();
				be.add("Votre compte est désactivé, impossible d'enchérir !");
				throw be;
			}
			
			auctionService.newAuction(articleId, bidOffer, userSession);

		} catch (BusinessException e) {
			e.getErreurs().forEach(err -> {
				String errorMessage = messageSource.getMessage(err, null, locale);
				redirectAttributes.addFlashAttribute("globalError", errorMessage);
			});
		}
		return redirectUrl;

	}
}
