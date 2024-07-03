package fr.eni.tp.encheres.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Article;
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
	public String showAuctions(@ModelAttribute("article") Article article, Model model) {
		List<Article> articlesList;
		if (article == null || (article.getArticleName().isEmpty() && (article.getCategory() == null || article.getCategory().getCategoryId() == 0))) {
			articlesList = auctionService.findArticles();
		} else if(article.getArticleName().isEmpty()) {
			articlesList = auctionService.findArticlesByCategory(article.getCategory());
		} else if(article.getCategory() == null || article.getCategory().getCategoryId() == 0) {
			articlesList = auctionService.findArticlesByName(article.getArticleName());
		} else {
			articlesList = auctionService.findArticlesByCategoryAndName(article.getCategory(), article.getArticleName());
		}
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
	public String showArticleCreation(@ModelAttribute("article") Article article, @ModelAttribute("userSession") User userSession) {
		
		article.setAuctionStartDate(LocalDateTime.of(article.getStartDateTemp(), article.getStartTimeTemp()));
		article.setAuctionEndDate(LocalDateTime.of(article.getEndDateTemp(), article.getEndTimeTemp()));
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
