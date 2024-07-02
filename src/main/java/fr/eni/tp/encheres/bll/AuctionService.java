package fr.eni.tp.encheres.bll;

import java.util.List;

import fr.eni.tp.encheres.bo.Article;
import fr.eni.tp.encheres.bo.Auction;
import fr.eni.tp.encheres.bo.Category;

public interface AuctionService {
	Article findArticleById(int articleId);
	List<Article> findArticlesByName(String name);
	List<Article> findArticlesByCategory(Category category);
	List<Article> findArticles();
	void sell(Article article);
	void deleteArticle(int articleId);
	void updateArticle(Article article);
	
	Category findCategoryById(int categoryId);
//	Category findCategoryByName(String name);
	List<Category> findCategories();
	void newCategory(Category category);
	void updateCategory(Category category);
	
	List<Auction> findAuctions(int userId, int articleId);
	List<Auction> findAllAuctions(int articleId);
	void newAuction(Auction auction);
	void deleteAuction(Auction auction);

}
