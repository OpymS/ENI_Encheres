package fr.eni.tp.encheres.dal;

import java.util.HashMap;
import java.util.List;

import fr.eni.tp.encheres.bo.Article;

public interface ArticleDAO {
	Article read(int articleId);
	List<Article> findAll();
	List<Article> findByCategory(int categoryId);
	List<Article> findByCategoryAndName(int categoryId, String name);
	List<Article> findByName(String name);
	void create(Article article);
	void delete(int articleId);
	void updateArticle(Article article);
	void updateSellPriceAndBuyer(int articleId, int bidAmount, int userId);
	int countArticles();
	List<Article> findArticleToUpdateToFinished();
	List<Article> findArticleToUpdateToStarted();
	List<Article> findWithFilters(Article article, HashMap<String, Boolean> filters, String buyOrSale, int userId);
	int countArticlesByBuyerId(int userId);
	int countArticlesFinishedBySellerId(int userId);
}
