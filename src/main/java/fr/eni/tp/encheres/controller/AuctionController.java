package fr.eni.tp.encheres.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

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
		System.out.println("showAuctions début");
		List<Article> articlesList = auctionService.selectArticles(article, userSession, open, current, won, currentVente, notstarted, finished, buySale);
		
		model.addAttribute("article", article);
		model.addAttribute("articles", articlesList);
		model.addAttribute("open", open);
		model.addAttribute("current", current);
		model.addAttribute("won", won);
		model.addAttribute("currentVente", currentVente);
		model.addAttribute("notstarted", notstarted);
		model.addAttribute("finished", finished);
		boolean sales = false;
		boolean purchases = false;
		if (buySale != null && buySale.equals("sales")) {
			sales = true;
		}
		if (buySale != null && buySale.equals("purchases")) {
			purchases = true;
		}
		model.addAttribute("sales", sales);
		model.addAttribute("purchases", purchases);
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
	public String showArticleCreation(@Valid @ModelAttribute("article") Article article, BindingResult bindingResult, @ModelAttribute("userSession") User userSession,
			@RequestParam(name="startDateTemp", required=false) LocalDate startDate,
			@RequestParam(name="endDateTemp", required=false) LocalDate endDate,
			@RequestParam(name="startTimeTemp", required=false) LocalTime startTime,
			@RequestParam(name="endTimeTemp", required=false) LocalTime endTime
			) {
		LocalDateTime startDateTime;
		LocalDateTime endDateTime;
		
		if (bindingResult.hasErrors()) {
			return "article-create";
		} else {
			try {
				startDateTime = auctionService.convertDate(startDate, startTime);
				endDateTime = auctionService.convertDate(endDate, endTime);
				article.setAuctionStartDate(startDateTime);
				article.setAuctionEndDate(endDateTime);
				article.setSeller(userSession);
				article.setCurrentPrice(article.getBeginningPrice());			
				System.out.println(article);
			
			
				auctionService.sell(article);
				return "redirect:/auctions";
			} catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					ObjectError error = new ObjectError("globalError", err);
					bindingResult.addError(error);
				});
				return "article-create";
			}
			
		}
	}
	
	@GetMapping("/modifyArticle")
	public String showArticleModifyPage(@RequestParam(name="articleId", required=true) int articleId, Model model, @ModelAttribute("userSession") User userSession) {
		Article article = auctionService.findArticleById(articleId);
		
		if(article.getAuctionStartDate().isBefore(LocalDateTime.now())) { // Si on est après la date de départ on ne montre pas la page !
			return "redirect:/auctions";
		};
		
		//PickupLocation defaultPickupLocation = new PickupLocation(userSession.getStreet(), userSession.getZipCode(), userSession.getCity());
		//article.setPickupLocation(defaultPickupLocation);
		
		model.addAttribute("article", article);
		model.addAttribute("startDate",article.getAuctionStartDate().toLocalDate());
		model.addAttribute("startTime",article.getAuctionStartDate().toLocalTime());
		model.addAttribute("endDate",article.getAuctionEndDate().toLocalDate());
		model.addAttribute("endTime",article.getAuctionEndDate().toLocalTime());
		return "article-modify";
	}
	
	@PostMapping("/modifyArticle")
	public String showArticleModifyPage(@Valid @ModelAttribute("article") Article article, 
			BindingResult bindingResult,
			@ModelAttribute("userSession") User userSession,
			@RequestParam(name="startDateTemp", required=false) LocalDate startDate,
			@RequestParam(name="endDateTemp", required=false) LocalDate endDate,
			@RequestParam(name="startTimeTemp", required=false) LocalTime startTime,
			@RequestParam(name="endTimeTemp", required=false) LocalTime endTime,
			Model model
			) {
		LocalDateTime startDateTime;
		LocalDateTime endDateTime;
		
		//On les repasse dans le modèle pour réafficher les valeurs par défaut en cas d'erreurs
		model.addAttribute("startDate",startDate);
		model.addAttribute("startTime",startTime);
		model.addAttribute("endDate",endDate);
		model.addAttribute("endTime",endTime);
		
		if (bindingResult.hasErrors()) {
			
			
			return "article-modify";
		} else {
			try {
				startDateTime = auctionService.convertDate(startDate, startTime);
				endDateTime = auctionService.convertDate(endDate, endTime);
				article.setAuctionStartDate(startDateTime);
				article.setAuctionEndDate(endDateTime);
				article.setSeller(userSession);
				article.setCurrentPrice(article.getBeginningPrice());			
				//System.out.println(article);
				
				auctionService.updateArticle(article);
				return "redirect:/auctions";
				
			} catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					ObjectError error = new ObjectError("globalError", err);
					bindingResult.addError(error);
				});
				return "article-modify";
			}
			
		}
	}

	@ModelAttribute("categoriesSession")
	public List<Category> loadCategories() {
		List<Category> categories = auctionService.findCategories();
		return categories;
	}
}
