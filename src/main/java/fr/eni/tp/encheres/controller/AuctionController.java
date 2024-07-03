package fr.eni.tp.encheres.controller;

import java.time.LocalDateTime;

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
		List<Article> articlesList;
		System.out.println("bouton radio : " + buySale);
		System.out.println("case open : " + open);
		System.out.println("case current : " + current);
		System.out.println("case won : " + won);
		System.out.println("case curtentVente : " + currentVente);
		System.out.println("case notstarted : " + notstarted);
		System.out.println("case finished : " + finished);
		if (userSession == null || userSession.getUserId() == 0) {
			if (article == null || (article.getArticleName().isEmpty()
					&& (article.getCategory() == null || article.getCategory().getCategoryId() == 0))) {
				articlesList = auctionService.findArticles();
			} else if (article.getArticleName().isEmpty()) {
				articlesList = auctionService.findArticlesByCategory(article.getCategory());
			} else if (article.getCategory() == null || article.getCategory().getCategoryId() == 0) {
				articlesList = auctionService.findArticlesByName(article.getArticleName());
			} else {
				articlesList = auctionService.findArticlesByCategoryAndName(article.getCategory(),
						article.getArticleName());
			}
		} else {
			if (article == null || (article.getArticleName().isEmpty()
					&& (article.getCategory() == null || article.getCategory().getCategoryId() == 0))) {
				articlesList = auctionService.findArticles();
			} else if (article.getArticleName().isEmpty()) {
				articlesList = auctionService.findArticlesByCategory(article.getCategory());
			} else if (article.getCategory() == null || article.getCategory().getCategoryId() == 0) {
				articlesList = auctionService.findArticlesByName(article.getArticleName());
			} else {
				articlesList = auctionService.findArticlesByCategoryAndName(article.getCategory(),
						article.getArticleName());
			}
			if (buySale != null && buySale.equals("sales")) {
				List<Article> tmpArticlesList = articlesList.stream()
						.filter(art -> art.getSeller().getUserId() == userSession.getUserId())
						.collect(Collectors.toList());

				LocalDateTime now = LocalDateTime.now();
				articlesList.clear();
				for (Article art : tmpArticlesList) {
					if (currentVente && art.getAuctionStartDate().isBefore(now)
							&& art.getAuctionEndDate().isAfter(now)) {
						articlesList.add(art);
					}
					if (notstarted && art.getAuctionStartDate().isAfter(now)) {
						articlesList.add(art);
					}
					if (finished && art.getAuctionEndDate().isBefore(now)) {
						articlesList.add(art);
					}
				}
			} else if (buySale != null && buySale.equals("purchases")) {
				List<Auction> auctionsList = auctionService.findAuctionsByUser(userSession.getUserId());
			}
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
