package fr.eni.tp.encheres.controller;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;

@Controller
@RequestMapping("/auctions")
@SessionAttributes({ "userSession", "categoriesSession" })
public class AuctionController {
	private AuctionService auctionService;

	public AuctionController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	@GetMapping
	public String showAuctionsPage(Model model) {
		Article article = new Article();
		model.addAttribute("article", article);
		return "auctions";
	}

	@PostMapping
	public String showAuctions(@ModelAttribute("article") Article article, Model model,
			@ModelAttribute("userSession") User userSession,
			@RequestParam(name = "open", required = false) boolean open,
			@RequestParam(name = "current", required = false) boolean current,
			@RequestParam(name = "won", required = false) boolean won,
			@RequestParam(name = "currentVente", required = false) boolean currentVente,
			@RequestParam(name = "notstarted", required = false) boolean notstarted,
			@RequestParam(name = "finished", required = false) boolean finished,
			@RequestParam(name = "achats-ventes", required = false) String buySale) {
		List<Article> articlesList = auctionService.selectArticles(article, userSession, open, current, won, currentVente, notstarted, finished, buySale);
		
		model.addAttribute("articles", articlesList);
		return "auctions";
	}

	@GetMapping("/newArticle")
	public String showArticleCreation(Model model, @ModelAttribute("userSession") User userSession) {
		Article article = new Article();
		PickupLocation defaultPickupLocation = new PickupLocation(userSession.getStreet(), userSession.getZipCode(), userSession.getCity());
		article.setPickupLocation(defaultPickupLocation);
		model.addAttribute("article", article);
		return "article-create";
	}
	
	@PostMapping("/newArticle")
	public String showArticleCreation(@ModelAttribute("article") Article article, @ModelAttribute("userSession") User userSession,
			@RequestParam(name="startDateTemp", required=true) LocalDate startDate,
			@RequestParam(name="endDateTemp", required=true) LocalDate endDate,
			@RequestParam(name="startTimeTemp", required=true) LocalTime startTime,
			@RequestParam(name="endTimeTemp", required=true) LocalTime endTime
			) {
		
		article.setAuctionStartDate(LocalDateTime.of(startDate, startTime));
		article.setAuctionEndDate(LocalDateTime.of(endDate, endTime));
		article.setSeller(userSession);
		article.setCurrentPrice(article.getBeginningPrice());
		
		System.out.println(article);
		
		auctionService.sell(article);
		return "redirect:/auctions";
	}

	@ModelAttribute("categoriesSession")
	public List<Category> loadCategories() {
		List<Category> categories = auctionService.findCategories();
		return categories;
	}
}
