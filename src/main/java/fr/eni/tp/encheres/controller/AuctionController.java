package fr.eni.tp.encheres.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import fr.eni.tp.encheres.bll.AuctionService;
import fr.eni.tp.encheres.bll.FileService;
import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.ArticleState;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.bo.dto.SearchCriteria;
import fr.eni.tp.encheres.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auctions")
@SessionAttributes({ "userSession", "categoriesSession", "research" })
public class AuctionController {
	private static final int PAGE_SIZE = 6;
	
	private AuctionService auctionService;
	private FileService fileService;
  private MessageSource messageSource;

	public AuctionController(AuctionService auctionService, FileService fileService, MessageSource messageSource) {
		this.auctionService = auctionService;
		this.fileService = fileService;
    this.messageSource = messageSource;
	

	@GetMapping
	public String showAuctionsPage(@ModelAttribute("research") SearchCriteria sessionResearch,
			@ModelAttribute("userSession") User userSession, Model model,
			@RequestParam(name = "currentPage", defaultValue = "1") int currentPage) {
		System.out.println("get " + sessionResearch);
		model.addAttribute("criteria", sessionResearch);
		Page<Article> articlesList = auctionService.selectArticles(sessionResearch, userSession.getUserId(),
				PageRequest.of(currentPage - 1, PAGE_SIZE));
		model.addAttribute("articles", articlesList);
		model.addAttribute("currentPage", currentPage);

		int totalPages = articlesList.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "auctions";
	}

	@PostMapping
	public String showAuctions(@ModelAttribute("criteria") SearchCriteria research,@ModelAttribute("research") SearchCriteria sessionResearch , Model model,
			@ModelAttribute("userSession") User userSession,
			@RequestParam(name = "currentPage", defaultValue = "1") int currentPage) {
		System.out.println("showAuctions début");
		System.out.println("post " + research);
		
		sessionResearch.setWordToFind(research.getWordToFind());
		sessionResearch.setCategory(research.getCategory());
		sessionResearch.setRadioButton(research.getRadioButton());
		sessionResearch.setFilters(research.getFilters());
		
		model.addAttribute("criteria", research);
		Page<Article> articlesList = auctionService.selectArticles(research, userSession.getUserId(),
				PageRequest.of(currentPage - 1, PAGE_SIZE));
		model.addAttribute("articles", articlesList);
		model.addAttribute("currentPage", currentPage);

		int totalPages = articlesList.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "auctions";
	}

	@GetMapping("/newArticle")
	public String showArticleCreation(Model model, @ModelAttribute("userSession") User userSession) {
		
		//Si utilisateur désactivé, on empèche l'enchère
		if(!userSession.isActivated()) {
			return "redirect:/auctions";
		}
		
		Article article = new Article();
		PickupLocation defaultPickupLocation = new PickupLocation(userSession.getStreet(), userSession.getZipCode(),
				userSession.getCity());
		article.setPickupLocation(defaultPickupLocation);
		model.addAttribute("article", article);
		return "article-create";
	}

	@PostMapping("/newArticle")

	public String showArticleCreation(@Valid @ModelAttribute("article") Article article, BindingResult bindingResult, @ModelAttribute("userSession") User userSession,
			@RequestParam(name="startDateTemp", required=false) LocalDate startDate,
			@RequestParam(name="endDateTemp", required=false) LocalDate endDate,
			@RequestParam(name="startTimeTemp", required=false) LocalTime startTime,
			@RequestParam(name="endTimeTemp", required=false) LocalTime endTime,
			@RequestParam(name="inputImage", required=false) MultipartFile fileImage,
			Locale locale) {
    
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
        //Sauvegarde de l'image dans le dossier static/uploadedImages
			  fileService.saveFile(fileImage, article);
				auctionService.sell(article);
				return "redirect:/auctions";
		} catch (IOException e) {
			//ObjectError error = new ObjectError("globalError", e.getMessage());
			return "article-create";
		}
		} catch (BusinessException e) {
			e.getErreurs().forEach(err -> {
					String errorMessage = messageSource.getMessage(err, null, locale);
					ObjectError error = new ObjectError("globalError", errorMessage);
					bindingResult.addError(error);
			});
			return "article-create";
		}
  }
}

	@GetMapping("/modifyArticle")

	public String showArticleModifyPage(
    @RequestParam(name="articleId", required=true) int articleId,
		Model model,
		@ModelAttribute("userSession") User userSession){
		
		//Si utilisateur désactivé, on empèche l'enchère
		if(!userSession.isActivated()) {
			return "redirect:/auctions";
		}
		
		Article article = auctionService.findArticleById(articleId);
    
		// Si on est après la date de fin on ne montre pas la page (modification/annulation impossible !)
		if(article.getAuctionEndDate().isBefore(LocalDateTime.now())) { 
			return "redirect:/auctions";
		};

		model.addAttribute("article", article);
		model.addAttribute("startDate",article.getAuctionStartDate().toLocalDate());
		model.addAttribute("startTime",article.getAuctionStartDate().toLocalTime());
		model.addAttribute("endDate",article.getAuctionEndDate().toLocalDate());
		model.addAttribute("endTime",article.getAuctionEndDate().toLocalTime());
		model.addAttribute("isCancelPossible", article.getState().equals(ArticleState.NOT_STARTED) || article.getState().equals(ArticleState.STARTED));
		model.addAttribute("imageSource", "/uploadedImages/"+article.getImageUUID());
		
		System.err.println("/uploadedImages/"+article.getImageUUID());
		
		return "article-modify";
	}

	@PostMapping("/modifyArticle")
	public String showArticleModifyPage(@Valid @ModelAttribute("article") Article article, BindingResult bindingResult,
			@ModelAttribute("userSession") User userSession,
			@RequestParam(name="startDateTemp", required=false) LocalDate startDate,
			@RequestParam(name="endDateTemp", required=false) LocalDate endDate,
			@RequestParam(name="startTimeTemp", required=false) LocalTime startTime,
			@RequestParam(name="endTimeTemp", required=false) LocalTime endTime,
			@RequestParam(name="inputImage", required=false) MultipartFile fileImage,
			Model model, 
      Locale locale) {
		
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
        
        //Sauvegarde de l'image dans le dossier static/uploadedImages
        fileService.saveFile(fileImage, article);
        
        auctionService.updateArticle(article);
				return "redirect:/auctions";
        
    } catch (IOException) {
			//ObjectError error = new ObjectError("globalError", e.getMessage());
			return "article-modify";
		}	catch (BusinessException e) {
				e.getErreurs().forEach(err -> {
					String errorMessage = messageSource.getMessage(err, null, locale);
					ObjectError error = new ObjectError("globalError", errorMessage);
					bindingResult.addError(error);
				});
				return "article-modify";
			}

		}
	}

	@GetMapping("/cancelArticle")
	public String cancelArticle(@RequestParam(name = "articleId") int articleId,
			@SessionAttribute("userSession") User userSession) {
		Article articleToCancel = auctionService.findArticleById(articleId);

		// On vérifie si c'est bien le vendeur de l'article qui accède à ce lien
		if (articleToCancel.getSeller().getUserId() == userSession.getUserId()) {

			auctionService.cancelArticle(articleToCancel);

		} else { // Sinon c'est un imposteur
			return "redirect:/auctions";
		}

		return "redirect:/auctions";
	}

	@ModelAttribute("categoriesSession")
	public List<Category> loadCategories() {
		List<Category> categories = auctionService.findCategories();
		return categories;
	}

	@ModelAttribute("research")
	public SearchCriteria research() {
		System.out.println("création du ModelAttribute research");
		SearchCriteria research = new SearchCriteria();
		research.setWordToFind("");
		Category category = new Category();
		research.setCategory(category);
		research.setRadioButton("purchases");
		Map<String, Boolean> filters = new HashMap<String, Boolean>();
		filters.put("open", true);
		filters.put("current", false);
		filters.put("won", false);
		filters.put("currentVente", false);
		filters.put("notstarted", false);
		filters.put("finished", false);
		research.setFilters(filters);
		return research;
	}
}
