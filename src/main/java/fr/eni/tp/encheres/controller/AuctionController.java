package fr.eni.tp.encheres.controller;

import java.util.List;
import java.util.stream.Collectors;

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

@Controller
@RequestMapping("/auctions")
@SessionAttributes({"userSession", "categoriesSession"})
public class AuctionController {
	private AuctionService auctionService;
	
	public AuctionController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	public String showAuctionsPage(Model model) {
		Article article = new Article();
		model.addAttribute("article", article);
		return "auctions";
	}
	
	@PostMapping
	public String showAuctions(@ModelAttribute("article") Article article, Model model) {
		System.out.println(article);
		String wordToFind = article.getArticleName();
		List<Article> articlesList = auctionService.findArticlesByName(wordToFind);
		if (article.getCategory().getCategoryId() != 0) {
			List<Article> articlesList2 = auctionService.findArticlesByCategory(article.getCategory());
			articlesList=articlesList.stream().filter(art->articlesList2.contains(art)).collect(Collectors.toList());
		}
		model.addAttribute("articles",articlesList);
		return "redirect:/auctions";
	}
	
	@GetMapping("/newArticle")
	public String showArticleCreation() {
		return "article-create";
	}
	
	@ModelAttribute("categoriesSession")
	public List<Category> loadCategories(){
		List<Category> categories = auctionService.findCategories();
		return categories;
	}
}
