package fr.eni.tp.encheres.dal;

import java.util.List;

import fr.eni.tp.encheres.bo.Article;

public interface ArticleDAO {
	Article read(long articleId);
	List<Article> findAll();
	List<Article> findByCategory(long categoryId);
	List<Article> findByName(String name);
	void create(Article article);
	void delete(long articleId);
}
