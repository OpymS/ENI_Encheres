package fr.eni.tp.encheres.bll;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.dal.ArticleDAO;
import fr.eni.tp.encheres.dal.AuctionDAO;
import fr.eni.tp.encheres.dal.CategoryDAO;
import fr.eni.tp.encheres.dal.PickUpLocationDAO;
import fr.eni.tp.encheres.dal.UserDAO;

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
	public void sell(Article article) {
		articleDAO.create(article);
		pickUpLocationDAO.create(article.getPickupLocation(), article.getArticleId());

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
		auction.setAuctionDate(LocalDateTime.now());
		// TODO à mettre dans le controller plus tard si besoin.
		auctionDAO.create(auction);
	}

	@Override
	public void deleteAuction(Auction auction) {
		auctionDAO.delete(auction.getUser().getUserId(), auction.getArticle().getArticleId(), auction.getAuctionDate());
	}

}
