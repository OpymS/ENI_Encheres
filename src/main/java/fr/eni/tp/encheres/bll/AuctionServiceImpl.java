package fr.eni.tp.encheres.bll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.PickupLocation;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.dal.ArticleDAO;
import fr.eni.tp.encheres.dal.AuctionDAO;
import fr.eni.tp.encheres.dal.CategoryDAO;
import fr.eni.tp.encheres.dal.PickUpLocationDAO;
import fr.eni.tp.encheres.dal.UserDAO;
import fr.eni.tp.encheres.exception.BusinessException;

@Service
public class AuctionServiceImpl implements AuctionService {

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
		Article article = articleDAO.read(articleId);
		article.setCategory(categoryDAO.readById(article.getCategory().getCategoryId()));
		article.setBids(auctionDAO.findByArticle(articleId));
		article.setPickupLocation(pickUpLocationDAO.findByArticleId(articleId));
		article.setSeller(userDAO.readById(article.getSeller().getUserId()));
		return article;
	}

	@Override
	public List<Article> findArticlesByName(String name) {
		List<Article> articlesList = articleDAO.findByName(name);
		articlesList.forEach(article -> {
			article.setCategory(categoryDAO.readById(article.getCategory().getCategoryId()));
			article.setBids(auctionDAO.findByArticle(article.getArticleId()));
			article.setPickupLocation(pickUpLocationDAO.findByArticleId(article.getArticleId()));
			article.setSeller(userDAO.readById(article.getSeller().getUserId()));
		});
		return articlesList;
	}


	@Override
	public List<Article> findArticlesByCategory(Category category) {
		List<Article> articlesList = articleDAO.findByCategory(category.getCategoryId());
		articlesList.forEach(article -> {
			article.setCategory(categoryDAO.readById(article.getCategory().getCategoryId()));
			article.setBids(auctionDAO.findByArticle(article.getArticleId()));
			article.setPickupLocation(pickUpLocationDAO.findByArticleId(article.getArticleId()));
			article.setSeller(userDAO.readById(article.getSeller().getUserId()));
		});
		return articlesList;
	}
	

	@Override
	public List<Article> findArticlesByCategoryAndName(Category category, String name) {
		List<Article> articlesList = articleDAO.findByCategoryAndName(category.getCategoryId(), name);
		articlesList.forEach(article -> {
			article.setCategory(categoryDAO.readById(article.getCategory().getCategoryId()));
			article.setBids(auctionDAO.findByArticle(article.getArticleId()));
			article.setPickupLocation(pickUpLocationDAO.findByArticleId(article.getArticleId()));
			article.setSeller(userDAO.readById(article.getSeller().getUserId()));
		});
		return articlesList;
	}

	
	@Override
	public List<Article> findArticles() {
		List<Article> articlesList = articleDAO.findAll();
		articlesList.forEach(article -> {
			article.setCategory(categoryDAO.readById(article.getCategory().getCategoryId()));
			article.setBids(auctionDAO.findByArticle(article.getArticleId()));
			article.setPickupLocation(pickUpLocationDAO.findByArticleId(article.getArticleId()));
			article.setSeller(userDAO.readById(article.getSeller().getUserId()));
		});
		return articlesList;
	}

	@Override
	public List<Article> selectArticles(Article article, User user, boolean open, boolean current, boolean won, boolean currentVente, boolean notstarted, boolean finished, String buySale) {
		List<Article> articlesList;
		
		// si pas de mot dans l'input et pas de catégorie choisie
		if (article == null || (article.getArticleName().isEmpty()
				&& (article.getCategory() == null || article.getCategory().getCategoryId() == 0))) {
			articlesList = this.findArticles();
		} else if (article.getArticleName().isEmpty()) { //si pas de mot dans l'input
			articlesList = this.findArticlesByCategory(article.getCategory());
		} else if (article.getCategory() == null || article.getCategory().getCategoryId() == 0) { // si pas de catégorie choisie 
			articlesList = this.findArticlesByName(article.getArticleName());
		} else {
			articlesList = this.findArticlesByCategoryAndName(article.getCategory(),
					article.getArticleName());
		}
		if (user != null && user.getUserId() != 0) {
			if (buySale != null && buySale.equals("sales")) {
				List<Article> tmpArticlesList = articlesList.stream()
						.filter(art -> art.getSeller().getUserId() == user.getUserId())
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
							if (current && !open &&  art.getAuctionStartDate().isBefore(now) && art.getAuctionEndDate().isAfter(now)) {
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
				System.out.println(articlesList);
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
		BusinessException be = new BusinessException();
		boolean isValid = false;		
		isValid = checkDates(article.getAuctionStartDate(), article.getAuctionEndDate(), be) && checkPickUpLocation(article.getPickupLocation(), be);
		
		if (isValid) {
			try {
				articleDAO.create(article);
				pickUpLocationDAO.create(article.getPickupLocation(), article.getArticleId());
				
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("Un problème est survenu lors de l'accès à la base de données");
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean checkDates(LocalDateTime startDate, LocalDateTime endDate, BusinessException be) {
		boolean isValid = false;
		LocalDateTime now = LocalDateTime.now().plusMinutes(1);
		if (startDate == null || endDate == null) {
			be.add("Vente impossible. Les dates de début et de fin d'enchères doivent être renseignées");
		} else if (startDate.isBefore(now)) {
			be.add("Vente impossible. Les enchères ne peuvent pas commencer avant maintenant");
		} else if (startDate.isAfter(endDate)) {
			be.add("Vente impossible. Les enchères doivent finir après avoir commencé");
		} else {
			isValid = true;
		}
		return isValid;
	}
	
	private boolean checkPickUpLocation(PickupLocation pickupLocation, BusinessException be) {
		boolean isValid = false;
		if (pickupLocation.getStreet().isEmpty() || pickupLocation.getZipCode().isEmpty() || pickupLocation.getCity().isEmpty()) {
			be.add("Vente impossible. Remplissez tous les champs du lieu de retrait de l'article");
		} else {
			isValid = true; 
		}
		return isValid;
	}
	
	@Override
	public void deleteArticle(int articleId) {
		articleDAO.delete(articleId);
	}

	@Override
	public void updateArticle(Article article) {
		// TODO
	}

	@Override
	public Category findCategoryById(int categoryId) {
		Category category = categoryDAO.readById(categoryId);
		return category;
	}

	@Override
	public List<Category> findCategories() {
		List<Category> categories = categoryDAO.findAll();
		return categories;
	}

	@Override
	public void newCategory(Category category) {
		categoryDAO.create(category);
	}

	@Override
	public void updateCategory(Category category) {
		categoryDAO.update(category);
	}

	@Override
	public List<Auction> findAuctions(int userId, int articleId) {
		List<Auction> auctions = auctionDAO.read(userId, articleId);
		auctions.forEach(auction->{
			auction.setArticle(articleDAO.read(articleId));
			auction.setUser(userDAO.readById(userId));
		});
		return auctions;
	}

	@Override
	public List<Auction> findAuctionsByUser(int userId) {
		List<Auction> auctions = auctionDAO.findByUser(userId);
		auctions.forEach(auction->{
			auction.setArticle(articleDAO.read(auction.getArticle().getArticleId()));
			auction.setUser(userDAO.readById(userId));
		});
		return auctions;
	}
	
	@Override
	public List<Auction> findAllAuctions(int articleId) {
		List<Auction> auctions = auctionDAO.findByArticle(articleId);
		auctions.forEach(auction->{
			auction.setArticle(articleDAO.read(articleId));
			auction.setUser(userDAO.readById(auction.getUser().getUserId()));
		});
		return auctions;
	}

	@Override
	public void newAuction(Auction auction) {
		boolean isNewBidHigher = auction.getArticle().getCurrentPrice() < auction.getBidAmount();
		boolean isBidDateBeforeEnd = auction.getAuctionDate().isBefore(auction.getArticle().getAuctionEndDate());
		
		if(isNewBidHigher && isBidDateBeforeEnd) { // Si les deux ok, on crée l'enchère
			auctionDAO.create(auction);
			articleDAO.updateSellPriceAndBuyer(auction.getArticle().getArticleId(), auction.getBidAmount(), auction.getUser().getUserId());
			
		}else { // Sinon un des deux, ou les deux sont false
			if(!isNewBidHigher) {
				System.err.println("Votre mise n'est pas supérieure à la précédente !");
			}else if(!isBidDateBeforeEnd) {
				System.err.println("La date de fin de l'enchère est passée. Mise impossible...");
			}
		}
	}

	@Override
	public void deleteAuction(Auction auction) {
		auctionDAO.delete(auction.getUser().getUserId(), auction.getArticle().getArticleId(), auction.getAuctionDate());
	}

	@Override
	public LocalDateTime convertDate(LocalDate date, LocalTime time) throws BusinessException {
		BusinessException be = new BusinessException();
		LocalDateTime dateTime;
		if (date != null && time != null) {
			dateTime = LocalDateTime.of(date, time);
		} else {
			be.add("Les dates et heures doivent être renseignées");
			throw be;
		}
		return dateTime;
	}

}
