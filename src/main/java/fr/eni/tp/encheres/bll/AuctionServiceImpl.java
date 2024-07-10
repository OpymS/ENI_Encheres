package fr.eni.tp.encheres.bll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.ArticleState;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.bo.dto.SearchCriteria;
import fr.eni.tp.encheres.dal.ArticleDAO;
import fr.eni.tp.encheres.dal.AuctionDAO;
import fr.eni.tp.encheres.dal.CategoryDAO;
import fr.eni.tp.encheres.dal.PickUpLocationDAO;
import fr.eni.tp.encheres.dal.UserDAO;
import fr.eni.tp.encheres.exception.BusinessException;

@Service
public class AuctionServiceImpl implements AuctionService {
	private static final Logger auctionServiceLogger = LoggerFactory.getLogger(AuctionServiceImpl.class);

	private AuctionDAO auctionDAO;
	private CategoryDAO categoryDAO;
	private ArticleDAO articleDAO;
	private PickUpLocationDAO pickUpLocationDAO;
	private UserDAO userDAO;

	public AuctionServiceImpl(AuctionDAO auctionDAO, CategoryDAO categoryDAO, ArticleDAO articleDAO,
			PickUpLocationDAO pickUpLocationDAO, UserDAO userDAO) {
		this.auctionDAO = auctionDAO;
		this.categoryDAO = categoryDAO;
		this.articleDAO = articleDAO;
		this.pickUpLocationDAO = pickUpLocationDAO;
		this.userDAO = userDAO;

	}

	@Override
	public Article findArticleById(int articleId) {
		auctionServiceLogger.info("méthode findArticleById");
		return articleDAO.read(articleId);
	}

	@Override
	public List<Article> findArticlesByName(String name) {
		auctionServiceLogger.info("méthode findArticlesByName");
		return articleDAO.findByName(name);
	}

	@Override
	public List<Article> findArticlesByCategory(Category category) {
		auctionServiceLogger.info("méthode findArticlesByCategory");
		return articleDAO.findByCategory(category.getCategoryId());
	}

	@Override
	public List<Article> findArticlesByCategoryAndName(Category category, String name) {
		auctionServiceLogger.info("méthode findArticlesByCategoryAndName");
		return articleDAO.findByCategoryAndName(category.getCategoryId(), name);

	}

	@Override
	public List<Article> findArticles() {
		auctionServiceLogger.info("méthode findArticles");
		return articleDAO.findAll();
	}
	
	@Override
	public Page<Article> selectArticles(SearchCriteria research, int userId, Pageable pageable){
		auctionServiceLogger.info("méthode selectArticles");
		if (research.getRadioButton()==null) {
			research.setRadioButton("purchases");
		}
		
		if (research.getFilters().isEmpty() && research.getRadioButton().equals("purchases")) {
			Map<String, Boolean> filters = new HashMap<String, Boolean>();
			filters.put("open", true);
			research.setFilters(filters);
		}
		if (research.getCategory()==null) {
			Category category = new Category();
			category.setCategoryId(0);
			research.setCategory(category);
		}
		List<Article> articleList = articleDAO.findWithFilters(research,userId);
		
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Article> shortArticleList;
		
		if (articleList.size()< startItem) {
			shortArticleList = Collections.emptyList();
		}else {
			int endIndex = Math.min(startItem+pageSize, articleList.size());
			shortArticleList = articleList.subList(startItem, endIndex);
		}
		
		Page<Article> articlesPage = new PageImpl<Article>(shortArticleList, PageRequest.of(currentPage, pageSize), articleList.size());
		
		return articlesPage;
			
	}

	

	@Override
	@Deprecated
	public List<Article> selectArticles(Article article, User user, boolean open, boolean current, boolean won,
			boolean currentVente, boolean notstarted, boolean finished, String buySale) {
		auctionServiceLogger.info("méthode selectArticles deprecated");
		List<Article> articlesList;

		// si pas de mot dans l'input et pas de catégorie choisie
		if (article == null || (article.getArticleName().isEmpty()
				&& (article.getCategory() == null || article.getCategory().getCategoryId() == 0))) {
			articlesList = this.findArticles();
		} else if (article.getArticleName().isEmpty()) { // si pas de mot dans l'input
			articlesList = this.findArticlesByCategory(article.getCategory());
		} else if (article.getCategory() == null || article.getCategory().getCategoryId() == 0) { 	// si pas de catégorie
																									// choisie
			articlesList = this.findArticlesByName(article.getArticleName());
		} else {
			articlesList = this.findArticlesByCategoryAndName(article.getCategory(), article.getArticleName());
		}
		if (user != null && user.getUserId() != 0) {
			if (buySale != null && buySale.equals("sales")) {
				List<Article> tmpArticlesList = articlesList.stream()
						.filter(art -> art.getSeller().getUserId() == user.getUserId()).collect(Collectors.toList());

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
				List<Auction> auctionsList = this.findAuctionsByUser(user.getUserId());
				List<Article> tmpArticlesList = new ArrayList<Article>();
				LocalDateTime now = LocalDateTime.now();
				if (open) {
					for (Article art : articlesList) {
						if (art.getAuctionStartDate().isBefore(now) && art.getAuctionEndDate().isAfter(now)) {
							tmpArticlesList.add(art);
						}
					}
				}
				for (Article art : articlesList) {
					for (Auction auct : auctionsList) {
						if (art.getArticleId() == auct.getArticle().getArticleId()) {
							if (current && !open && art.getAuctionStartDate().isBefore(now)
									&& art.getAuctionEndDate().isAfter(now)) {
								tmpArticlesList.add(art);
							}
							if (won && art.getAuctionEndDate().isBefore(now)) {
								tmpArticlesList.add(art);
							}
						}

					}
				}
				articlesList.clear();
				articlesList = tmpArticlesList;
			}
		}
		/*
		 * Sur cette partie, il reste à :
		 * ne sélectionner que les enchères gagnées (pour l'instant on sélectionne toutes les enchères finies auxquelles l'utilisateur a participé. 
		 * il y a un risque de doublon si l'utilisateur a posé plusieurs enchères sur 1 article.
		 */
		return articlesList;
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void sell(Article article) throws BusinessException {
		auctionServiceLogger.info("méthode sell");
		BusinessException be = new BusinessException();
		boolean isValid = false;
		isValid = checkDates(article.getAuctionStartDate(), article.getAuctionEndDate(), be)
				&& checkPickUpLocation(article.getPickupLocation(), be);

		if (isValid) {
			try {
				articleDAO.create(article);
				pickUpLocationDAO.create(article.getPickupLocation(), article.getArticleId());

			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean checkDates(LocalDateTime startDate, LocalDateTime endDate, BusinessException be) {
		auctionServiceLogger.info("méthode checkDates");
		boolean isValid = false;
		// On enlève 2 minutes pour se laisser le temps du traitement.
		LocalDateTime now = LocalDateTime.now().minusMinutes(2);
		if (startDate == null || endDate == null) {
			be.add("error.sales.nodate");
		} else if (startDate.isBefore(now)) {
			be.add("error.sales.begintooearly");
		} else if (startDate.isAfter(endDate)) {
			be.add("error.sales.endtooearly");
		} else {
			isValid = true;
		}
		return isValid;
	}

	private boolean checkPickUpLocation(PickupLocation pickupLocation, BusinessException be) {
		auctionServiceLogger.info("méthode checkPickUpLocation");
		boolean isValid = false;
		if (pickupLocation.getStreet().isEmpty() || pickupLocation.getZipCode().isEmpty()
				|| pickupLocation.getCity().isEmpty()) {
			be.add("error.sales.pickuplocation");
		} else {
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void deleteArticle(int articleId) {
		auctionServiceLogger.info("méthode deleteArticle");
		articleDAO.delete(articleId);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateArticle(Article article) throws BusinessException {
		auctionServiceLogger.info("méthode updateArticle");
		BusinessException be = new BusinessException();
		boolean isValid = false;
		isValid = checkDates(article.getAuctionStartDate(), article.getAuctionEndDate(), be)
				&& checkPickUpLocation(article.getPickupLocation(), be);

		if (isValid) {
			try {
				articleDAO.updateArticle(article);
				pickUpLocationDAO.updatePickUpLocationByArticleId(article.getArticleId(), article.getPickupLocation());

			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
	}

	@Override
	public Category findCategoryById(int categoryId) {
		auctionServiceLogger.info("méthode findCategoryById");
		Category category = categoryDAO.readById(categoryId);
		return category;
	}

	@Override
	public List<Category> findCategories() {
		auctionServiceLogger.info("méthode findCategories");
		List<Category> categories = categoryDAO.findAll();
		return categories;
	}

	@Override
	public void newCategory(Category category) {
		auctionServiceLogger.info("méthode newCategory");
		categoryDAO.create(category);
	}

	@Override
	public void updateCategory(Category category) {
		auctionServiceLogger.info("méthode updateCategory");
		categoryDAO.update(category);
	}

	@Override
	public List<Auction> findAuctions(int userId, int articleId) {
		auctionServiceLogger.info("méthode findAuctions");
		List<Auction> auctions = auctionDAO.read(userId, articleId);
		auctions.forEach(auction -> {
			auction.setArticle(articleDAO.read(articleId));
			auction.setUser(userDAO.readById(userId));
		});
		return auctions;
	}

	@Override
	public List<Auction> findAuctionsByUser(int userId) {
		auctionServiceLogger.info("méthode findAuctionsByUser");
		List<Auction> auctions = auctionDAO.findByUser(userId);
		auctions.forEach(auction -> {
			auction.setArticle(articleDAO.read(auction.getArticle().getArticleId()));
			auction.setUser(userDAO.readById(userId));
		});
		return auctions;
	}

	@Override
	public List<Auction> findAllAuctions(int articleId) {
		auctionServiceLogger.info("méthode findAllAuctions");
		List<Auction> auctions = auctionDAO.findByArticle(articleId);
		auctions.forEach(auction -> {
			auction.setArticle(articleDAO.read(articleId));
			auction.setUser(userDAO.readById(auction.getUser().getUserId()));
		});
		return auctions;
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void newAuction(int articleId, int bidOffer, User userSession) throws BusinessException {
		auctionServiceLogger.info("méthode newAuction");
		BusinessException be = new BusinessException();
		Auction newAuction = new Auction();
		
		Article article = findArticleById(articleId);
		User currentBuyer = article.getCurrentBuyer(); //Stocke de l'utilisateur courant
		newAuction.setAuctionDate(LocalDateTime.now());
		newAuction.setUser(userSession);
		newAuction.setArticle(article);
		newAuction.setBidAmount(bidOffer);
		
		boolean isValid = checkCredit(userSession, bidOffer, be) && checkBidAmount(newAuction, be) && checkBidDate(newAuction, be);

		if (isValid) { // Si ok, on crée l'enchère
			try {
				auctionDAO.create(newAuction);
				articleDAO.updateSellPriceAndBuyer(newAuction.getArticle().getArticleId(), newAuction.getBidAmount(),
						newAuction.getUser().getUserId());
				currentBuyer.setCredit(currentBuyer.getCredit()+article.getCurrentPrice());
				userSession.setCredit(userSession.getCredit()-bidOffer);
				userDAO.updateCredit(currentBuyer);
				userDAO.updateCredit(userSession);
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("error.database.access");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean checkCredit(User user, int bidOffer, BusinessException be) {
		auctionServiceLogger.info("méthode checkCredit");
		boolean isValid = false;
		if (user.getCredit() >= bidOffer) {
			isValid = true;
		} else {
			be.add("error.auction.notenoughcredit");
		}
		return isValid;
	}
	
	private boolean checkBidAmount(Auction auction, BusinessException be) {
		auctionServiceLogger.info("méthode checkBidAmount");
		boolean isValid = false;
		if (auction.getArticle().getCurrentPrice() < auction.getBidAmount()) {
			isValid = true;
		} else {
			be.add("error.auction.weakoffer");
		}
		return isValid;
	}

	private boolean checkBidDate(Auction auction, BusinessException be) {
		auctionServiceLogger.info("méthode checkBidDate");
		boolean isValid = false;
		if (auction.getAuctionDate().isBefore(auction.getArticle().getAuctionEndDate())) {
			isValid = true;
		} else {
			be.add("error.auction.toolate");
		}
		return isValid;
	}

	@Override
	public void deleteAuction(Auction auction) {
		auctionServiceLogger.info("méthode deleteAuction");
		auctionDAO.delete(auction.getUser().getUserId(), auction.getArticle().getArticleId(), auction.getAuctionDate());
	}

	@Override
	public LocalDateTime convertDate(LocalDate date, LocalTime time) throws BusinessException {
		auctionServiceLogger.info("méthode convertDate");
		BusinessException be = new BusinessException();
		LocalDateTime dateTime;
		if (date != null && time != null) {
			dateTime = LocalDateTime.of(date, time);
		} else {
			be.add("error.missingdate");
			throw be;
		}
		return dateTime;
	}
	
	
	@Override
	public void cancelArticle(Article article) {
		auctionServiceLogger.info("méthode cancelArticle");
		if(article.getState().equals(ArticleState.NOT_STARTED)) {
			
			//personne à rembourser si pas commencée
			article.setState(ArticleState.CANCELED);
			articleDAO.updateArticle(article);
			
		}else if(article.getState().equals(ArticleState.STARTED)){
			
			// modifié article ET remboursé l'acheteur courant
			User currentBuyer = article.getCurrentBuyer();
			currentBuyer.setCredit(currentBuyer.getCredit() + article.getCurrentPrice());
			userDAO.updateCredit(currentBuyer);
			
			article.setState(ArticleState.CANCELED);
			articleDAO.updateArticle(article);
			
			
		}else {
			//Trouver un moyen de notifié l'utilisateur que ce n'est pas possible
			//Après l'utilisateur ne devrai pas avoir accès au bouton d'annulation d'une vente donc ....
			auctionServiceLogger.error("Impossible d'annuler la vente ! Déjà annulée ou terminée ! Article id : " + article.getArticleId());
		}
	}

}
