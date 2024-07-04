package fr.eni.tp.encheres.bll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;
import fr.eni.tp.encheres.bo.User;
import fr.eni.tp.encheres.exception.BusinessException;

public interface AuctionService {
	Article findArticleById(int articleId);
	List<Article> findArticlesByName(String name);
	List<Article> findArticlesByCategory(Category category);
	List<Article> findArticlesByCategoryAndName(Category category, String name);
	List<Article> findArticles();
	List<Article> selectArticles(Article article, User user, boolean open, boolean current, boolean won, boolean currentVente, boolean notstarted, boolean finished, String buySale);
	void sell(Article article);
	void deleteArticle(int articleId);
	void updateArticle(Article article);
	
	Category findCategoryById(int categoryId);
//	Category findCategoryByName(String name);
	List<Category> findCategories();
	void newCategory(Category category);
	void updateCategory(Category category);
	
	List<Auction> findAuctions(int userId, int articleId);
	List<Auction> findAuctionsByUser(int userId);
	List<Auction> findAllAuctions(int articleId);
	void newAuction(Auction auction);
	void deleteAuction(Auction auction);
	
	LocalDateTime convertDate(LocalDate date, LocalTime time) throws BusinessException;

}
